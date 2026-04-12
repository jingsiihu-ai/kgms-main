package com.hjc.kgms.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hjc.kgms.enums.ResourceType;
import com.hjc.kgms.service.MetadataExtractService;
import com.hjc.kgms.util.MetadataExtractResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MetadataExtractServiceImpl implements MetadataExtractService {

    private static final int MAX_SEARCH_TEXT = 8000;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Tika tika = new Tika();
    private final AutoDetectParser parser = new AutoDetectParser();

    @Override
    public MetadataExtractResult extract(MultipartFile file) {
        MetadataExtractResult result = new MetadataExtractResult();
        Map<String, Object> metadataMap = new HashMap<>();
        try {
            byte[] bytes = file.getBytes();
            String filename = file.getOriginalFilename();
            String mime = tika.detect(bytes, filename);
            ResourceType resourceType = detectType(mime, filename);

            metadataMap.put("originalFilename", filename);
            metadataMap.put("size", file.getSize());
            metadataMap.put("mimeType", mime);

            if (resourceType == ResourceType.IMAGE) {
                appendImageMetadata(metadataMap, bytes);
            }

            String textContent = "";
            try {
                org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
                ContentHandler handler = new BodyContentHandler(MAX_SEARCH_TEXT);
                parser.parse(new ByteArrayInputStream(bytes), handler, metadata, new ParseContext());
                textContent = handler.toString();
                appendDocumentMetadata(metadataMap, metadata);
            } catch (Exception ignored) {
            }
            if (StringUtils.isNotBlank(textContent) && textContent.length() > MAX_SEARCH_TEXT) {
                textContent = textContent.substring(0, MAX_SEARCH_TEXT);
            }

            result.setMimeType(mime);
            result.setResourceType(resourceType);
            result.setSearchableText(textContent == null ? "" : textContent);
            result.setMetadataJson(toJson(metadataMap));
            return result;
        } catch (Exception ex) {
            result.setMimeType(file.getContentType());
            result.setResourceType(detectType(file.getContentType(), file.getOriginalFilename()));
            result.setSearchableText("");
            result.setMetadataJson("{}");
            return result;
        }
    }

    private ResourceType detectType(String mime, String filename) {
        String lowerMime = mime == null ? "" : mime.toLowerCase(Locale.ROOT);
        String lowerName = filename == null ? "" : filename.toLowerCase(Locale.ROOT);

        if (lowerMime.startsWith("image/") || lowerName.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)$")) {
            return ResourceType.IMAGE;
        }
        if (lowerMime.startsWith("video/") || lowerName.matches(".*\\.(mp4|avi|mov|mkv)$")) {
            return ResourceType.VIDEO;
        }
        if (lowerMime.startsWith("audio/") || lowerName.matches(".*\\.(mp3|wav|aac|flac)$")) {
            return ResourceType.AUDIO;
        }
        if (lowerMime.contains("pdf") || lowerMime.contains("word") || lowerMime.contains("text")
                || lowerName.matches(".*\\.(doc|docx|txt|pdf|ppt|pptx|xls|xlsx)$")) {
            return ResourceType.DOCUMENT;
        }
        return ResourceType.OTHER;
    }

    private void appendImageMetadata(Map<String, Object> metadataMap, byte[] bytes) {
        try {
            com.drew.metadata.Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
            int count = 0;
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (count > 20) {
                        return;
                    }
                    metadataMap.put(tag.getTagName(), tag.getDescription());
                    count++;
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void appendDocumentMetadata(Map<String, Object> metadataMap, org.apache.tika.metadata.Metadata metadata) {
        String[] keys = {"title", "dc:title", "Author", "meta:author", "creator"};
        for (String key : keys) {
            String value = metadata.get(key);
            if (StringUtils.isNotBlank(value)) {
                metadataMap.put(key, value);
            }
        }
    }

    private String toJson(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

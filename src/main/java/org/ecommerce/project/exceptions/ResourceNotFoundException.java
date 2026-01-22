package org.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String resourceName;
    private String fieldName;
    private String field;
    private long fieldId;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String field) {
        super(String.format("%s not found with %s:%s", resourceName,fieldName, field));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.field = field;
    }

    public ResourceNotFoundException(String resourceName, String fieldName, long fieldId) {
        super(String.format("%s not found with %s:%d", resourceName,fieldName, fieldId));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldId = fieldId;
    }
}

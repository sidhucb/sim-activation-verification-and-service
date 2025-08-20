create table sim_activation (id serial primary key ,userName text not null , fileName; // Uploaded ID file name
image_data, filePath, String extractedText,  kycStatus;// Pending, Verified, Rejected
    
String aiValidation;

String simStatus; // Requested, Verified, Suspended

LocalDateTime createdAt;

LocalDateTime updatedAt);
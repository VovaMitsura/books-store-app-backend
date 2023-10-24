package com.example.booksstoreappbackend.s3;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@AllArgsConstructor
public class S3Service {

  private final S3Client s3Client;
  private final S3Buckets s3Buckets;

  public void putObject(String key, byte[] file) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Buckets.getCustomer())
            .key(key)
            .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
  }

  public byte[] getObject(String key) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Buckets.getCustomer())
            .key(key)
            .build();

    ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);

    try {
      return object.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean deleteObject(String key) {
    var response = s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(s3Buckets.getCustomer())
            .key(key)
            .build());

    return response.sdkHttpResponse().isSuccessful();
  }
}

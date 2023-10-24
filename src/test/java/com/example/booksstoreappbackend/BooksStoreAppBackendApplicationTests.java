package com.example.booksstoreappbackend;

import com.example.booksstoreappbackend.s3.S3Buckets;
import com.example.booksstoreappbackend.s3.S3Config;
import com.example.booksstoreappbackend.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BooksStoreAppBackendApplicationTests {

	@MockBean
	S3Service s3Service;
	@MockBean
	S3Buckets s3Buckets;
	@MockBean
	S3Config s3Config;

	@Test
	void contextLoads() {
	}

}

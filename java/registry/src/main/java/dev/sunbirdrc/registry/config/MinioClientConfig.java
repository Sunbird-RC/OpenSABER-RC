package dev.sunbirdrc.registry.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "filestorage.enabled", havingValue = "true", matchIfMissing = true)
public class MinioClientConfig {

	private static final Logger logger = LoggerFactory.getLogger(MinioClientConfig.class);
	@Value("${filestorage.url}")
	String url;
	@Value("${filestorage.accesskey}")
	String accessKey;
	@Value("${filestorage.secretkey}")
	String secretKey;
	@Value("${filestorage.bucketname}")
	String bucketName;

	@Bean("minioClient")
	public MinioClient minioClient() {
		MinioClient minioClient = MinioClient.builder()
				.endpoint(url)
				.credentials(accessKey, secretKey)
				.build();
		try {
			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
			if (!found) {
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			} else {
				logger.info("Minio bucket already exists: {}", bucketName);
			}
		} catch (Exception e) {
			logger.error("Minio initialization failed: {}", ExceptionUtils.getStackTrace(e));
		}
		return minioClient;
	}
}

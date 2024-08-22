package com.example.demo.service;

import com.example.demo.dto.ResponseDtoV1;
import com.example.demo.dto.UserEsDtoV1;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${elastic.search.user.index}")
    private String elasticSearchUserIndex;

    @Value("${elastic.search.auth.header}")
    private String elasticSearchAuthHeader;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public UserServiceImpl() {

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        elasticSearchAuthHeader)};

//        new RestHighLevelClient(
//                RestClient.builder(
//                                new HttpHost("localhost", 9200, "http"))
//                        .setDefaultHeaders(defaultHeaders));

        this.elasticsearchRestTemplate = new ElasticsearchRestTemplate(new RestHighLevelClient(
                RestClient.builder(
                                new HttpHost("localhost", 9200, "http"))
                        .setDefaultHeaders(defaultHeaders)));

    }

    @Override
    public ResponseDtoV1<?> validateUser(String jwtToken) {

        ResponseDtoV1<String> responseDto = new ResponseDtoV1<>();

        if (StringUtils.isEmpty(jwtToken)) {

            responseDto.setSuccess(false);
            responseDto.setMessage("Token must not be empty.");
            responseDto.setHttpStatus(HttpStatus.BAD_REQUEST);

            return responseDto;

        }

        Key secretKey = null;

        try {

            byte[] keyBytes = jwtSecretKey.getBytes();
            secretKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");

        } catch (Exception ex) {

            logger.error("*** Exception occurred while creating key instance *** \n" +
                    "Exception is => {}", Arrays.toString(ex.getStackTrace()));

            responseDto.setSuccess(false);
            responseDto.setMessage("Some exception occurred.");
            responseDto.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return responseDto;

        }

        Claims claims = null;

        try {

            claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

        } catch (Exception ex) {

            logger.error("*** Exception occurred while extracting claims from jwt token ***\n" +
                    "Exception is => {}", Arrays.toString(ex.getStackTrace()));

        }

        if (claims == null) {

            responseDto.setSuccess(false);
            responseDto.setMessage("Some exception occurred.");
            responseDto.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return responseDto;

        }

        String username = claims.get("username", String.class);
        String name = claims.get("name", String.class);
        String age = claims.get("age", String.class);

        UserEsDtoV1 userEsDto = new UserEsDtoV1();

        userEsDto.setUsername(username);
        userEsDto.setName(name);
        userEsDto.setAge(age);

        this.createUserEs(userEsDto);

//        Jwts.parser().verifyWith().build()
//        String subjectFromToken = Jwts.parser().verifyWith(keyFactory.generatePublic())
//                .build().parseSignedClaims(secretKey).getPayload().getSubject();

        return responseDto;

    }

    @Override
    public ResponseDtoV1<?> fetchUserFromEs(String id) {

        ResponseDtoV1<UserEsDtoV1> responseDto = new ResponseDtoV1<>();

        if (StringUtils.isEmpty(id)) {

            responseDto.setSuccess(false);
            responseDto.setMessage("Id must not be empty.");
            responseDto.setHttpStatus(HttpStatus.BAD_REQUEST);

            return responseDto;

        }

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        elasticSearchAuthHeader)};

        this.elasticsearchRestTemplate = new ElasticsearchRestTemplate(new RestHighLevelClient(
                RestClient.builder(
                                new HttpHost("localhost", 9200, "http"))
                        .setDefaultHeaders(defaultHeaders)));

        UserEsDtoV1 userEsDto = new UserEsDtoV1();

        try {

            userEsDto = elasticsearchRestTemplate.get(id, UserEsDtoV1.class, IndexCoordinates.of(elasticSearchUserIndex));

        } catch (Exception ex) {

            logger.error("*** Exception occurred while making fetch by id call to ES for id {} ***\n" +
                    "Exception is => {}", id, Arrays.toString(ex.getStackTrace()));

            responseDto.setSuccess(false);
            responseDto.setMessage("Some exception occurred.");
            responseDto.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return responseDto;

        }

        responseDto.setSuccess(true);
        responseDto.setMessage("Fetched data successfully.");
        responseDto.setData(userEsDto);
        responseDto.setHttpStatus(HttpStatus.OK);

        return responseDto;

    }

    @Override
    public boolean createUserEs(UserEsDtoV1 userDto) {

        if (userDto == null) {
            return false;
        }

        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        elasticSearchAuthHeader)};

        this.elasticsearchRestTemplate = new ElasticsearchRestTemplate(new RestHighLevelClient(
                RestClient.builder(
                                new HttpHost("localhost", 9200, "http"))
                        .setDefaultHeaders(defaultHeaders)));

        try {

            elasticsearchRestTemplate.save(userDto, IndexCoordinates.of(elasticSearchUserIndex));

        } catch (Exception ex) {

            logger.error("*** Exception occurred while saving document to elasticsearch ***\n" +
                    "Exception is => {}", Arrays.toString(ex.getStackTrace()));

            return false;

        }

        return true;

    }

}

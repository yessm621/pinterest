package com.pinterest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("테스트 도구 - Form 데이터 인코더")
@Import({FormDataEncoder.class, ObjectMapper.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = FormDataEncoder.class)
class FormDataEncoderTest {

    private final FormDataEncoder formDataEncoder;

    public FormDataEncoderTest(@Autowired FormDataEncoder formDataEncoder) {
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("객체를 넣으면, url encoding 된 form body data 형식의 문자열을 돌려준다.")
    @Test
    void givenObject_whenEncoding_thenReturnsFormEncodedString() {
        // Given
        TestObject obj = new TestObject(
                "This 'is' \"test\" string.",
                List.of("hello", "my", "friend").toString().replace(" ", ""),
                String.join(",", "hello", "my", "friend"),
                null,
                1234,
                3.14,
                false,
                BigDecimal.TEN,
                TestEnum.THREE
        );

        // When
        String result = formDataEncoder.encode(obj);

        // Then
        assertThat(result).isEqualTo(
                "str=This%20'is'%20%22test%22%20string." +
                        "&listStr1=%5Bhello,my,friend%5D" +
                        "&listStr2=hello,my,friend" +
                        "&nullStr" +
                        "&number=1234" +
                        "&floatingNumber=3.14" +
                        "&bool=false" +
                        "&bigDecimal=10" +
                        "&testEnum=THREE"
        );
    }

    @Data
    @AllArgsConstructor
    static class TestObject {
        private String str;
        private String listStr1;
        private String listStr2;
        private String nullStr;
        private Integer number;
        private Double floatingNumber;
        private Boolean bool;
        private BigDecimal bigDecimal;
        private TestEnum testEnum;
    }

    enum TestEnum {
        ONE, TWO, THREE
    }
}

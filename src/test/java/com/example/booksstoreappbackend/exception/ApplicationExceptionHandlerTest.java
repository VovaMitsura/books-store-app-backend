package com.example.booksstoreappbackend.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ApplicationExceptionHandlerTest {

    @Autowired
    ApplicationExceptionHandler exceptionHandler;

    @Test
    void when_entity_exception_passed_error_code_equals_response(){
        var entityException = new EntityConflictException(ApplicationExceptionHandler.DUPLICATE_ENTRY,
                "Exception occurred when saving duplicate");

        var response = exceptionHandler.handleEntityConflictException(entityException);

        Assertions.assertEquals(entityException.getErrorCode(), response.getErrorCode());
        Assertions.assertEquals(entityException.getMessage(), response.getErrorMessage());
    }

    @Test
    void when_not_found_exception_passed_error_code_equals_response(){
        var notFound = new NotFoundException(ApplicationExceptionHandler.NOT_FOUND,
                "Template not found");

        var response = exceptionHandler.handleNotFoundException(notFound);

        Assertions.assertEquals(notFound.getErrorCode(), response.getErrorCode());
        Assertions.assertEquals(notFound.getMessage(), response.getErrorMessage());
    }

    @Test
    void when_mailing_exception_passed_error_code_equals_response(){
        var mailing = new MailingException(ApplicationExceptionHandler.MAILING_EXCEPTION,
                "Could not send message to user bbb@mail.moc");

        var response = exceptionHandler.handleMailingException(mailing);

        Assertions.assertEquals(mailing.getErrorCode(), response.getErrorCode());
        Assertions.assertEquals(mailing.getMessage(), response.getErrorMessage());
    }

}

package com.spring.example.exception;

public class MissingAnnotationException extends  Exception
{
    private final String message;

    public MissingAnnotationException(String message, Throwable cause)
    {
        super(message, cause);
        this.message = message;
    }

    public MissingAnnotationException(String message)
    {
        super(message);
        this.message = message;
    }
}

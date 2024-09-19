package com.example.blog.common.exception

class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)

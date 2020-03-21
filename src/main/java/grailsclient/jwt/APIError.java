package grailsclient.jwt;

import groovy.transform.CompileStatic;

@CompileStatic
enum APIError {
    NONE, UNAUTHORIZED, FORBIDDEN, JSON_PARSING_ERROR, NETWORKING_ERROR
}
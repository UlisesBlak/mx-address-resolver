package io.github.ulisesblak.mxaddressresolver.exception;

public class CodigoPostalNoEncontradoException extends RuntimeException {

    public CodigoPostalNoEncontradoException(String cp) {
        super("No se encontro el codigo postal: " + cp);
    }
}
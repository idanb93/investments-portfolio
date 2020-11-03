package assets.asset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AssetNotFound extends RuntimeException {
    @ControllerAdvice
    static class AssetNotFoundHandler {
        @ResponseBody
        @ExceptionHandler(AssetNotFound.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String assetNotFoundHandler(AssetNotFound exceptionMessage) {
            return exceptionMessage.getMessage();
        }
    }
    public AssetNotFound(Long id){
        super("Could not find asset: " + id);
    }
}
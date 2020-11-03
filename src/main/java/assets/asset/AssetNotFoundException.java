package assets.asset;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AssetNotFoundException extends RuntimeException {

    @ControllerAdvice
    static class AssetNotFoundHandler {
        @ResponseBody
        @ExceptionHandler(AssetNotFoundException.class)
        @ResponseStatus(HttpStatus.NOT_FOUND)
        String assetNotFoundHandler(AssetNotFoundException ex){
            return ex.getMessage();
        }
    }

    AssetNotFoundException(Long id){
        super("Could not find asset: " + id);
    }

}

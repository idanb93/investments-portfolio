package assets.asset;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class RESTfulController {

    private final DataBase dataBase;
    private final EntityAdapter myAssetAdapter;

    public RESTfulController(DataBase db, EntityAdapter myAssetAdapter){
        this.dataBase = db;
        this.myAssetAdapter = myAssetAdapter;
    }

    @GetMapping("/assets")
    CollectionModel<EntityModel<Asset>> getAllAssets(){
        List<EntityModel<Asset>> assets = dataBase.findAll().stream()
                .map(myAssetAdapter::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(assets,
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withSelfRel());
    }

    // Get Asset By ID

    @GetMapping("/assets/{id}")
    EntityModel<Asset> getSingleAsset(@PathVariable Long id){
        Asset asset = dataBase.findById(id).orElseThrow(()->new AssetNotFound(id));
        return myAssetAdapter.toModel(asset);
    }

    // Get Asset by Name
    // http://localhost:8080/assets/byname?name=""
    @GetMapping("/assets/byname")
    public Asset getAssetByName(@RequestParam String name){
        return dataBase.getAssetByName(name);
    }

    // https://localhost:8080/assets/byticker?ticker=""
    @GetMapping("/assets/byticker")
    public Asset getAssetByTicker(@RequestParam String ticker){
        return dataBase.getAssetByTicker(ticker);
    }

    // Get All Assets (Ordered by highest percentage)

    @GetMapping("/assets/percentage")
    CollectionModel<EntityModel<Asset>> getAssetsByPercentageSorted(){
        List<EntityModel<Asset>> assets =
                dataBase.findAll().stream()
//                        .filter(asset -> asset.setPercentage())
                        .sorted(Comparator.reverseOrder())
                        .map(asset -> myAssetAdapter.toModel(asset))
                        .collect(Collectors.toList());

        return new CollectionModel<>(assets,
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withSelfRel());
    }

    // Get Only CryptoCurrencies

    @GetMapping("/assets/cryptocurrency")
    CollectionModel<EntityModel<Asset>> getCryptoAssets(){
        List<EntityModel<Asset>> assets =
                dataBase.findAll().stream()
                        .filter(asset -> asset.getTypeOfAsset().equals("Crypto"))
                        .sorted(Comparator.reverseOrder())
                        .map(asset -> myAssetAdapter.toModel(asset))
                        .collect(Collectors.toList());

        return new CollectionModel<>(assets,
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withSelfRel());
    }

    // Get Only Stocks

    @GetMapping("/assets/stocks")
    CollectionModel<EntityModel<Asset>> getStocksAssets(){
        List<EntityModel<Asset>> assets =
                dataBase.findAll().stream()
                        .filter(asset -> asset.getTypeOfAsset().equals("Stock"))
                        .sorted(Comparator.reverseOrder())
                        .map(asset -> myAssetAdapter.toModel(asset))
                        .collect(Collectors.toList());

        return new CollectionModel<>(assets,
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withSelfRel());
    }

    // Adding a new Asset

    @PostMapping("/assets")
    ResponseEntity<EntityModel<Asset>> newAsset(@RequestBody Asset asset){
        Asset savedAsset = dataBase.save(asset);
        return ResponseEntity.created(linkTo(methodOn(RESTfulController.class).getSingleAsset(savedAsset.getId())).toUri())
                .body(myAssetAdapter.toModel(savedAsset));
    }

    // Deleting an Asset

    @DeleteMapping("/assets/{id}")
    ResponseEntity<Void> deleteAsset(@PathVariable Long id){
        dataBase.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Update an Asset

    @PutMapping("/assets/{id}")
    ResponseEntity<?> updateAsset(@RequestBody Asset newAsset, @PathVariable Long id){
        Asset oldAsset = dataBase.findById(id)
                .map(new Function<Asset, Asset>() {
                    @Override
                    public Asset apply(Asset oldAsset) {
                        oldAsset.lastModified = LocalDateTime.now();
                        oldAsset.setAmount(newAsset.getAmount());
                        oldAsset.percentage = oldAsset.setPercentage();
                        return dataBase.save(oldAsset);
                    }
                })
                .orElseGet(()->{
                    return dataBase.save(newAsset);
                });

        return ResponseEntity.created(linkTo(methodOn(RESTfulController.class).getSingleAsset(oldAsset.getId())).toUri())
                .body(myAssetAdapter.toModel(oldAsset));
    }
}

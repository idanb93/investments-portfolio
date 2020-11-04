package assets.asset;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
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

    @GetMapping("/assets/{id}")
    EntityModel<Asset> getSingleAsset(@PathVariable Long id){
        Asset asset = dataBase.findById(id).orElseThrow(()->new AssetNotFound(id));
        return myAssetAdapter.toModel(asset);
    }

    @GetMapping("/assets/byname")
    public Asset getAssetByName(@RequestParam String name){
        return dataBase.getAssetByName(name);
    }

    @GetMapping("/assets/byticker")
    public Asset getAssetByTicker(@RequestParam String ticker){
        return dataBase.getAssetByTicker(ticker);
    }

    @GetMapping("/assets/percentage")
    CollectionModel<EntityModel<Asset>> getAssetsByPercentageSorted(){
        List<EntityModel<Asset>> assets =
                dataBase.findAll().stream()
                        .sorted(Comparator.reverseOrder())
                        .map(asset -> myAssetAdapter.toModel(asset))
                        .collect(Collectors.toList());

        return new CollectionModel<>(assets,
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withSelfRel());
    }

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

    @PostMapping("/assets")
    ResponseEntity<EntityModel<Asset>> newAsset(@RequestBody Asset newAsset) throws IOException {

        Asset savedAsset = dataBase.save(new Asset(newAsset.getName(),newAsset.getTicker(),newAsset.getAmount(),newAsset.getTypeOfAsset()));

        for (Asset currentAsset : dataBase.findAll()){
            currentAsset.lastModified = LocalDateTime.now();
            currentAsset.percentage = currentAsset.setPercentage();
            dataBase.save(currentAsset);
        }

        return ResponseEntity.created(linkTo(methodOn(RESTfulController.class).getSingleAsset(savedAsset.getId())).toUri())
                .body(myAssetAdapter.toModel(savedAsset));
    }

    @DeleteMapping("/assets/{id}")
    ResponseEntity<Void> deleteAsset(@PathVariable Long id){

        BigDecimal amount1 = BigDecimal.valueOf(dataBase.findById(id).get().getAmount());
        Asset.totalValue = Asset.totalValue.subtract( (dataBase.findById(id).get().getPrice().multiply(amount1)) );

        dataBase.deleteById(id);

        for (Asset currentAsset : dataBase.findAll()){
            currentAsset.lastModified = LocalDateTime.now();
            currentAsset.percentage = currentAsset.setPercentage();
            dataBase.save(currentAsset);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/assets/{id}")
    ResponseEntity<?> updateAsset(@RequestBody Asset newAsset, @PathVariable Long id){
        Asset oldAsset = dataBase.findById(id)
                .map(new Function<Asset, Asset>() {
                    @Override
                    public Asset apply(Asset oldAsset) {

                        oldAsset.setAmount(newAsset.getAmount());
                        oldAsset.setTotalValue();
                        Asset oldAsset1 = dataBase.save(oldAsset);

                        for (Asset currentAsset : dataBase.findAll()){
                            currentAsset.lastModified = LocalDateTime.now();
                            currentAsset.percentage = currentAsset.setPercentage();
                            dataBase.save(currentAsset);
                        }

                        return oldAsset1;
                    }
                })
                .orElseGet(()->{
                    return dataBase.save(newAsset);
                });

        return ResponseEntity.created(linkTo(methodOn(RESTfulController.class).getSingleAsset(oldAsset.getId())).toUri())
                .body(myAssetAdapter.toModel(oldAsset));
    }
}

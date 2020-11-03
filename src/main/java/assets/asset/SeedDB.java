package assets.asset;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SeedDB {
    @Bean
    CommandLineRunner initDatabase(DataBase repository) {

        return args -> {

            repository.save(new Asset("VeChain", "VET-USD", 5303.0, "Crypto"));
            repository.save(new Asset("Stellar", "XLM-USD", 1129.0, "Crypto"));
            repository.save(new Asset("Cosmos", "ATOM1-USD",16.18, "Crypto"));
            repository.save(new Asset("Qtum", "QTUM-USD",29.13,"Crypto"));
            repository.save(new Asset("Ontology", "ONT-USD",83.0,"Crypto"));
            repository.save(new Asset("Nano", "NANO-USD",37.0,"Crypto"));
            repository.save(new Asset("TomoChain", "TOMO-USD",35.08,"Crypto"));
            repository.save(new Asset("0x", "ZRX-USD",8.0,"Crypto"));
            repository.save(new Asset("Lemonade", "LMND",11.0, "Stock"));
            repository.save(new Asset("JFROG", "FROG",8.0, "Stock"));

            for (Asset asset : repository.findAll()){
                asset.percentage = asset.setPercentage();
                repository.save(asset);
            }

        };
    }

}

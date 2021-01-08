package assets.asset;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SeedDB {
    @Bean
    CommandLineRunner initDatabase(DataBase repository) {

        return args -> {

            repository.save(new Asset("Polkadot", "DOT2-USD", 74.51, "CryptoCurrency"));
            repository.save(new Asset("Elrond", "EGLD-USD", 19.99, "CryptoCurrency"));
            repository.save(new Asset("Theta", "THETA-USD", 109.89, "CryptoCurrency"));
            repository.save(new Asset("Celsius", "CEL-USD", 24.8, "CryptoCurrency"));
            repository.save(new Asset("ChainLink", "LINK-USD", 14.92, "CryptoCurrency"));
            repository.save(new Asset("VeChain", "VET-USD", 4900.0, "CryptoCurrency"));
            repository.save(new Asset("Stellar", "XLM-USD", 800.0, "CryptoCurrency"));
            repository.save(new Asset("Cosmos", "ATOM1-USD",16.18, "CryptoCurrency"));
            repository.save(new Asset("TomoChain", "TOMO-USD",50.0,"CryptoCurrency"));
            repository.save(new Asset("Lemonade", "LMND",10.0, "Stock"));
            repository.save(new Asset("DropBox", "DBX",30.0, "Stock"));

            for (Asset asset : repository.findAll()){
                asset.percentage = asset.setPercentage();
                repository.save(asset);
            }

        };
    }

}

package assets.asset;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DataBase extends JpaRepository<Asset,Long> {

    /**
     * CRUD operations in DATABASE - Create, Read, Update, Delete
     *
     * Will be implemented in RESTController:
     * CRUD in HTTP according to HTTP verb
     * Create - Post
     * Update - Put
     * Delete - Delete
     * Get - Get Representation of a Resource
     */

    Asset getAssetByName(String name);
    Asset getAssetByTicker(String ticker);
}
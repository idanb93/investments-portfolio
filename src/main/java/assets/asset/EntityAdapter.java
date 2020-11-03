package assets.asset;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EntityAdapter implements RepresentationModelAssembler<Asset, EntityModel<Asset>> {
    @Override
    public EntityModel<Asset> toModel(Asset asset) {
        return new EntityModel<>(asset,
                linkTo(methodOn(RESTfulController.class).getSingleAsset(asset.getId())).withSelfRel(),
                linkTo(methodOn(RESTfulController.class).getAllAssets()).withRel("assets"));
    }
}

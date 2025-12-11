package druyaned.aston.intensive.userservice.web;

import druyaned.aston.intensive.userservice.model.UserDto;
import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

/**
 * Provides conversions from {@link UserDto} to {@link EntityModel} and from list of user
 * DTOs to {@link CollectionModel} to assemble links in {@code get} and {@code getAll} methods of
 * the controller.
 *
 * @author druyaned
 * @see UserController
 */
@Component
public class UserModelAssembler implements
        RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(UserDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).get(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAll(null)).withRel("users"));
    }

    public CollectionModel<EntityModel<UserDto>> toCollectionModel(List<UserDto> users) {
        List<EntityModel<UserDto>> userModels = users.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).getAll(null)).withSelfRel());
    }
}

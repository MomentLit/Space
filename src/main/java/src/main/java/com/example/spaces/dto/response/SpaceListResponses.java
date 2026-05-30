package src.main.java.com.example.spaces.dto.response;



import java.util.List;

public record SpaceListResponses(
        List<SpaceListResponse> spaces
) {

    public static SpaceListResponses from(List<Space> spaces) {
        return new SpaceListResponses(
                spaces.stream()
                        .map(SpaceListResponse::from)
                        .toList()
        );
    }
}
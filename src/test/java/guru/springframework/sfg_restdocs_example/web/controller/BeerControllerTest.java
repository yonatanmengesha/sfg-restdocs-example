package guru.springframework.sfg_restdocs_example.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfg_restdocs_example.domain.Beer;
import guru.springframework.sfg_restdocs_example.repositories.BeerRepository;
import guru.springframework.sfg_restdocs_example.web.model.BeerDto;
import guru.springframework.sfg_restdocs_example.web.model.BeerStyleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
 */

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriScheme = "https",uriHost = "dev.springframework.guru",uriPort = 80)
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "guru.springframework.sfg_restdocs_example.web.mappers")
class BeerControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerRepository beerRepository;

    @Test
    void getBeerById() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
        given(beerRepository.findById(any())).willReturn(Optional.of(Beer.builder().build()));

        mockMvc.perform(get("/api/v1/beer/{beerId}" , UUID.randomUUID().toString())
                        .param("iscold","yes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/beer-get",pathParameters(
                 parameterWithName("beerId").description("UUID of desired beer to get")
                ),requestParameters(
                        parameterWithName("iscold").description("Is Beer Cold Query Parameter")
                        )
                        ,responseFields(
                                fieldWithPath("id").description("Id of Beer"),
                                fieldWithPath("version").description("Version Number"),
                                fieldWithPath("createdDate").description("Date Created"),
                                fieldWithPath("lastModifiedDate").description("Date Updated"),
                                fieldWithPath("beerName").description("Beer Name"),
                                fieldWithPath("beerStyle").description("Beer Style"),
                                fieldWithPath("upc").description("UPC of Beer").attributes(),
                                fieldWithPath("price").description("Price"),
                                fieldWithPath("quantityOnHand").description("Quantity On Hand")
                        )
                ));
    }

    @Test
    void saveNewBeer() throws Exception {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        ConstrainedFields fields = new ConstrainedFields(BeerDto.class);
        mockMvc.perform(post("/api/v1/beer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJson))
                .andExpect(status().isCreated())
                .andDo(document("v1/beer-new",
                        requestFields(
                                fields.withPath("id").ignored(),
                                fields.withPath("version").ignored(),
                                fields.withPath("createdDate").ignored(),
                                fields.withPath("lastModifiedDate").ignored(),
                                fields.withPath("beerName").description("Name of the Beer"),
                                fields.withPath("beerStyle").description("style of beer"),
                                fields.withPath("upc").description("BEER UPC").attributes(),
                                fields.withPath("price").description("Beer Price"),
                                fields.withPath("quantityOnHand").ignored()
                        )));
//                                responseFields(
//                                        fields.withPath("id").ignored(),
//                                        fields.withPath("version").ignored(),
//                                        fields.withPath("createdDate").ignored(),
//                                        fields.withPath("lastModifiedDate").ignored(),
//                                        fields.withPath("beerName").description("Name of the Beer"),
//                                        fields.withPath("beerStyle").description("style of beer"),
//                                        fields.withPath("upc").description("BEER UPC").attributes(),
//                                        fields.withPath("price").description("Beer Price"),
//                                        fields.withPath("quantityOnHand").ignored()
//                                )));
        // the above is the use of manual implementation of fileds.withPath
//                .andDo(document("v1/beer",
//                        requestFields(
//                                fieldWithPath("id").ignored(),
//                                fieldWithPath("version").ignored(),
//                                fieldWithPath("createdDate").ignored(),
//                                fieldWithPath("lastModifiedDate").ignored(),
//                                fieldWithPath("beerName").description("Name of the Beer"),
//                                fieldWithPath("beerStyle").description("style of beer"),
//                                fieldWithPath("upc").description("BEER UPC").attributes(),
//                                fieldWithPath("price").description("Beer Price"),
//                                fieldWithPath("quantityOnHand").ignored()
//                        )));
    }

    @Test
    void updateBeerById() throws Exception {
        BeerDto beerDto =  getValidBeerDto();
        String beerDtoJson = objectMapper.writeValueAsString(beerDto);

        mockMvc.perform(put("/api/v1/beer/" + UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJson))
                .andExpect(status().isNoContent());
    }

    BeerDto getValidBeerDto(){
        return BeerDto.builder()
                .beerName("Nice Ale")
                .beerStyle(BeerStyleEnum.ALE)
                .price(new BigDecimal("9.99"))
                .upc(123123123123L)
                .build();

    }

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }
}
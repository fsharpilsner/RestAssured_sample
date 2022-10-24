package Test_Pkg;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.hamcrest.number.OrderingComparison;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class Basic_Response {

public  static  final String base_URL ="https://catfact.ninja/fact";

        @Test
        public void test_Method()
        {
            final String base_URL2 ="https://datausa.io/api/data?drilldowns=Nation&measures=Population";
            Response response = RestAssured.get(base_URL2);
            //assertEquals( 200,response.getStatusCode());            //assertEquals ("application/json" , response.getContentType());
            //assertEquals ("application/json; charset=utf-8" , response.getContentType());
            //ResponseBody<?> body = response.getBody();
            //System.out.println(body.prettyPrint());
            //System.out.println(response.asString());
            //System.out.println(response.getHeaders().toString());
            //header is not body. response = body
            JsonPath jpath = response.body().jsonPath();

            //JSONParser parser = new JSONParser();

            //Map <String, String> fullJson = jpath.get();
            //System.out.println(fullJson);

            // A multipart nested tag is always ArrayList<String>
            // Deepest tag which is not nested is simple Map <k,v>
            //ArrayList<<LinkedHashMap>>, serialized to LinkedHashMap
            //Map <String, String> subJson = jpath.get();
            //System.out.println(subJson.get("source")); //Nested Multimap
            //System.out.println(subJson.get("annotations")); //OK--  Not nested, but why return everything?
            // Map <String, String> subJson = jpath.get("annotations"); //OK, Deepest tag Map<k,v>

            //ArrayList<<LinkedHashMap>>, serialized to LinkedHashMap
            //Map <String, Object> subJson = jpath.get("source"); //NOT OK
            //Map <String, Map<?,?>> subJson = jpath.get("source"); //NOT OK
            //Map <String, ArrayList<String>> subJson = jpath.get("source.measures"); //Not OK
            //System.out.println(subJson);
            /*
            List<String> json_data = new ArrayList<>();

            for (Object value : subJson.values()) {
                ;
                //json_data.add(value.toString());
            }
            //System.out.println(json_data);
            */
            //ArrayList<String> json_flat = jpath.get("source.annotations");
            //System.out.println(json_flat);

            //System.out.println(json_flat.toString());

        }

    @Test
    public void validate_Nested() {

            final String base_URL2 = "https://datausa.io/api/data?drilldowns=Nation&measures=Population";
            Response response = (Response) RestAssured.get(base_URL2)
                    .then()
                    /*
                    .rootPath("source.annotations") //.extract().response()
                    .assertThat()
                    .body("topic", containsString("Diversity")); //null?? Actual: <[null]>
                    //I think the error is because it expects a list with 1 string element. not just 1 string.
                    */
                    .rootPath("data")
                    .assertThat()
                    //.header("x-ratelimit-limit", Integer::parseInt, equalTo(100))


                    //.body("data.ID Nation[0]", equalTo("01000US"));
                    //Reason: We cannot insert function here. We have to make a Matcher object.
                    //.body("Population[1]", Integer::parseInt, equalTo(328239523))

                    .body("Population[2]", Matchers.equalTo(325719178));


    }

        @Test
        public  void github_post_Test()
        {
            final String Github_URL = "https://api.github.com/user/issues";
            final String G_token = "ghp_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

            //from: https://docs.github.com/en/enterprise-cloud@latest/rest/overview/other-authentication-methods
            // curl -v -H "Authorization: token TOKEN" https://api.github.com/user/issues
            // This is basically to formulate the above. That works from command line.
            RestAssured

                    .given()
                             .header("Authorization: " , "token "+ G_token)
                             .body("{ \"name\" : \"RestAssured_post_test\"}")
                    .when()
                        .post(Github_URL)
                    .then()
                        .statusCode(201);



        }


        @Test
        public  void test_Options()
        {
            RestAssured.options(base_URL)

                    .then()
                    .log().body()
                    .log().headers()

                    //.statusCode(200)
                    .statusCode(HttpStatus.SC_OK)
                    .log().body()
                    .log().headers()

                    .header("X-Content-Type-Options", equalTo("nosniff"))
                    .body(emptyOrNullString());

        }


        @Test
        public  void generic_Headers()
        {
            Response response = RestAssured.get(base_URL);
            assertEquals(response.getHeader("server"),"nginx");

        }

        @Test
        public  void genric_Content()
        {
            Response response = RestAssured.get(base_URL);
            assertEquals(response.getHeader("x-ratelimit-limit"),"101");

        }

        @Test
        public  void get_Headers()
        {
            Response response = RestAssured.get(base_URL);
            Headers headers = response.getHeaders();
            //System.out.println(headers);
            List<Header> list = headers.asList();
            boolean isPresent = headers.hasHeaderWithName("x-frame-options");
            assertTrue(!isPresent);


        }

        @Test
        public void get_Time()
        {
            Response response = RestAssured.get(base_URL );
            System.out.println(response.getHeaders().toString());
            System.out.println(response.getTime());
            System.out.println(response.getTimeIn(TimeUnit.MILLISECONDS));


        }

        @Test
        public  void get_Fluid()
        {

            //System.out.println(response.getHeaders().toString()); //asList<> too
            RestAssured.get(base_URL)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    //.header("x-ratelimit-limit", "100");
                    //.header("x-ratelimit-limit", Integer::parseInt, lessThanOrEqualTo(100));
                    .header("x-ratelimit-limit", Integer::parseInt, equalTo(100))
                    .header("x-ratelimit-Remaining",
                    response-> greaterThan(response.header("X-Ratelimit-limit")))

                    .header("Date",
                            date-> LocalDate.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME),
                            OrderingComparison.comparesEqualTo(LocalDate.now()));


                    //.header("X-Ratelimit-Limit"),

            //OrderingComparison.comparesEqualTo(LocalDate.of(2001, 1, 1)));
            //server returns the string: "100". then Integer.parseInt("100") give the integer 100

        }


        @Test
        public  void hamcrest_Example()
        {
            assertThat (10 , lessThan(11));

        }

        @Test
        public  void cat_Info()
        {
            //Response response = RestAssured.get(base_URL );
            //response.header()
            RestAssured.given().when().get(base_URL).then()
                    //.body("length", lessThan("657"));
                    .header("X-Frame-Options", notNullValue());


        }

    @Test
    public void cat_Map()

    {
        Map<String, String> expectedHeaders = Map.of("Connection", "keep-alive",
                "Content-Type", "application/json");
        RestAssured.get(base_URL)
                .then()
                .headers("Connection", "keep-alive",
                        "Content-Type", "application/json" ,
                        "Cache-Control", equalTo("no-cache, private"))
                //more keys,vals can be added here. For some reason contains("...") doesn't work!
                .headers(expectedHeaders);
    }
/*
    @org.testng.annotations.Test
    public void getList(){
        given(requestSpecification).
                basePath("/gmail/v1").pathParam("userId","kalle.pelle@gmail.com").

                when().
                    get("/users/{userId}/messages").
                then().spec(responseSpecification);
    }
*/
@Test
    public void  JsonPath_Cat()
    {
        Response response = RestAssured.get(base_URL);
        JsonPath jpath = response.body().jsonPath();

    }

};



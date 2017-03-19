package com.hackit.api.github;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class GitHubTests {

	private static Client client;

	@BeforeClass
	public static void getToken() {

		ClientConfig clientConfig = new ClientConfig();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("xtermous@gmail.com", "rbd@6321P");

		clientConfig.register(feature);

		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget webTarget = client.target("https://api.github.com").path("authorizations");
		AuthorizationRequest authorizationRequest = new AuthorizationRequest(new String[] { "public_repo", "user", "public_repo", "repo",
																			"repo_deployment", "repo:status", "delete_repo",
																			"gist", "admin:repo_hook", "admin:org_hook",
																			"admin:org", "admin:public_key", "admin:gpg_key" },
																			"Blackie41");

		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

		try {
			String payLoad = objectWriter.writeValueAsString(authorizationRequest);
			System.out.println("Token Request Message: \n" + payLoad);
			Response response = webTarget.request("application/json").post(Entity.json(payLoad));
			String ResponseFromAuthorization = response.readEntity(String.class);
			ObjectMapper objectMapper = new ObjectMapper();
			Object json = objectMapper.readValue(ResponseFromAuthorization,Object.class);
			String responseWithToken = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
			System.out.println("Token Response Message: \n" + responseWithToken);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(ResponseFromAuthorization);
			String token = root.path("token").asText();

			assertEquals(201, response.getStatus());

			ClientConfig oauthClientConfig = new ClientConfig();
			Feature oauthfeature = OAuth2ClientSupport.feature(token);
			oauthClientConfig.register(oauthfeature);
			GitHubTests.client = ClientBuilder.newClient(oauthClientConfig);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void followUser() {

		WebTarget followUser = GitHubTests.client.target("https://api.github.com").path("user/following").path("rockydadin");
		Entity<?> empty = Entity.text("");
		Response followUserResponse = followUser.request().put(empty);

		assertEquals(204, followUserResponse.getStatus());

		WebTarget followCheck = client.target("https://api.github.com").path("user/following").path("rockydadin");
		Response followCheckResponse = followCheck.request().get();

		assertEquals(204, followCheckResponse.getStatus());

	}

	@Test
	public void unfollowUser() {

		WebTarget followUser = client.target("https://api.github.com").path("user/following").path("rockydadin");
		Response followUserResponse = followUser.request().delete();
		
		assertEquals(204, followUserResponse.getStatus());

		WebTarget followCheck = client.target("https://api.github.com").path("user/following").path("rockydadin");
		Response followCheckResponse = followCheck.request().get();

		assertEquals(404, followCheckResponse.getStatus());

	}
	
	@Test
	public void commentOnIssue(){
		
		try {
			
		WebTarget commentOnIssueTarget = client.target("https://api.github.com").path("repos/ramanujd/KnowYourEMI/issues/2").path("comments");
		
		CommentOnIssue commentOnIssue = new CommentOnIssue("It can be better optimized if we use no server storage.");
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String requestMsgForComment = objectWriter.writeValueAsString(commentOnIssue);
		System.out.println("Comment Request Message: \n"+requestMsgForComment);
		
		Response commentOnIssueResponse = commentOnIssueTarget.request("application/json").post(Entity.json(requestMsgForComment));
		String ResponseFromCreateComment = commentOnIssueResponse.readEntity(String.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		Object json = objectMapper.readValue(ResponseFromCreateComment,Object.class);
		String responseWithComment = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
		System.out.println("Token Response Message: \n" + responseWithComment);
		
		
		assertEquals(201, commentOnIssueResponse.getStatus());
		
		
		} catch (JsonProcessingException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
}

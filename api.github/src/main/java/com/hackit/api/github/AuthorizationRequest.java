package com.hackit.api.github;

public class AuthorizationRequest {
	
	public String client_secret;
	public String[] scopes;
	public String note;
	

	
	public AuthorizationRequest(String[] scopes, String note) {
		super();
		this.scopes = scopes;
		this.note = note;
		
	}
	
}

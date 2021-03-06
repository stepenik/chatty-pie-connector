/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chattypie.service.chattypie.chatroom;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class ChatroomMembershipService {
	private static final String CHATROOM_USERS_RESOURCE_ENDPOINT_TEMPLATE = "%s/rooms/%s/users/%s";

	private final RestTemplate restTemplate;
	private final String chattyPieHost;

	public void assignUserToChatroom(String chatroomId, String userEmail) {
		restTemplate.postForLocation(
				format(CHATROOM_USERS_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, chatroomId, userEmail),
				""
		);
	}

	public void unassignUserFromChatroom(String chatroomId, String userEmail) {
		restTemplate.delete(
				format(CHATROOM_USERS_RESOURCE_ENDPOINT_TEMPLATE, chattyPieHost, chatroomId, userEmail)
		);
	}
}

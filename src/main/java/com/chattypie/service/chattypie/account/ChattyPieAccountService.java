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

package com.chattypie.service.chattypie.account;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;

import lombok.RequiredArgsConstructor;

import org.springframework.web.client.RestTemplate;

import com.chattypie.util.Delayer;

@RequiredArgsConstructor
public class ChattyPieAccountService {
	private static final int MAX_ALLOWED_ROOMS = 100;

	private final RestTemplate restTemplate;
	private final String chattyPieHost;
	private final Delayer delayer;

	public ChattyPieAccount createChattyPieAccount() {
		ChattyPieAccount chattyPieAccount = restTemplate.postForObject(
				format("%s/accounts", chattyPieHost),
				format("{\"max_allowed_rooms\": %d}", MAX_ALLOWED_ROOMS),
				ChattyPieAccount.class
		);

		waitUntilAccountIsPropagatedOnTheirNetwork();

		return chattyPieAccount;
	}

	private void waitUntilAccountIsPropagatedOnTheirNetwork() {
		delayer.delayFor(ofSeconds(5));
	}
}

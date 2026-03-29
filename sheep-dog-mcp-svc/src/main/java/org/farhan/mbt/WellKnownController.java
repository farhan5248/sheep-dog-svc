/*
* Copyright 2024 - 2024 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.farhan.mbt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to handle .well-known requests.
 * Returns 404 for OAuth discovery endpoints to signal no authentication required.
 */
@RestController
@RequestMapping("/.well-known")
public class WellKnownController {

	@GetMapping("/oauth-authorization-server")
	public ResponseEntity<Void> oauthAuthorizationServer() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	@GetMapping("/**")
	public ResponseEntity<Void> wellKnownCatchAll() {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

}

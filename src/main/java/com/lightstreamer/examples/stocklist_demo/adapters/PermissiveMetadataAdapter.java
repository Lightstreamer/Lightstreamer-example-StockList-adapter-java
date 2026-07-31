/*
*
* Copyright (c) Lightstreamer Srl
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.lightstreamer.examples.stocklist_demo.adapters;

import com.lightstreamer.adapter.common.InitException;
import com.lightstreamer.adapter.metadata.CreditsException;
import com.lightstreamer.adapter.metadata.LoginInfo;
import com.lightstreamer.adapter.metadata.MetadataAdapter;
import com.lightstreamer.adapter.metadata.SessionRequestHandler;
import com.lightstreamer.adapter.metadata.SubscriptionHandler;
import com.lightstreamer.adapter.metadata.SubscriptionInfo;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A basic implementation of {@link MetadataAdapter} with permissive defaults.
 * Lacks authentication and is suitable only for demos.
 */
public class PermissiveMetadataAdapter implements MetadataAdapter {

    @Override
    public void init(Map<String, String> params, File configDir) throws InitException {
    }

    @Override
    public CompletionStage<SessionRequestHandler> loginAsync(LoginInfo loginInfo) {
        return CompletableFuture.completedFuture(new SessionRequestHandler() {

            @Override
            public Double getAllowedMaxBandwidth() {
                return null;
            }

            @Override
            public CompletionStage<SubscriptionHandler> newSubscription(SubscriptionInfo info) {
                return CompletableFuture.completedFuture(new SubscriptionHandler(info));
            }

            @Override
            public CompletionStage<String> userMessageAsync(String message) throws CreditsException {
                throw new CreditsException(0, "Unsupported feature");
            }
        });
    }

    @Override
    public void shutdown() {
    }
}

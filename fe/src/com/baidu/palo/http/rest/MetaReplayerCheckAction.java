// Copyright (c) 2017, Baidu.com, Inc. All Rights Reserved

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.http.rest;

import com.baidu.palo.catalog.Catalog;
import com.baidu.palo.common.DdlException;
import com.baidu.palo.http.ActionController;
import com.baidu.palo.http.BaseRequest;
import com.baidu.palo.http.BaseResponse;
import com.baidu.palo.http.IllegalArgException;

import org.codehaus.jackson.map.ObjectMapper;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;

/*
 * used to get meta replay info
 * eg:
 *  fe_host:http_port/api/_meta_replay_state
 */
public class MetaReplayerCheckAction extends RestBaseAction {
    public MetaReplayerCheckAction(ActionController controller) {
        super(controller);
    }

    public static void registerAction(ActionController controller) throws IllegalArgException {
        MetaReplayerCheckAction action = new MetaReplayerCheckAction(controller);
        controller.registerHandler(HttpMethod.GET, "/api/_meta_replay_state", action);
    }

    @Override
    public void execute(BaseRequest request, BaseResponse response) throws DdlException {
        checkAdmin(request);

        Map<String, String> resultMap = Catalog.getInstance().getMetaReplayState().getInfo();

        // to json response
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.writeValueAsString(resultMap);
        } catch (Exception e) {
            //  do nothing
        }

        // send result
        response.setContentType("application/json");
        response.getContent().append(result);
        sendResult(request, response);
    }
}
/*******************************************************************************
 * Copyright 2016 General Electric Company.
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
 *******************************************************************************/

package com.ge.predix.acs.monitoring;

import static com.ge.predix.acs.commons.web.AcsApiUriTemplates.HEARTBEAT_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

/**
 *
 * @author 212360328
 */
@RestController
public class AcsMonitoringController {

    @ApiOperation(value = "Monitoring API that allows to check the ACS heartbeat", tags = { "Monitoring" })
    @RequestMapping(method = GET, value = HEARTBEAT_URL, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getHeartBeat() {
        return "alive";
    }

}

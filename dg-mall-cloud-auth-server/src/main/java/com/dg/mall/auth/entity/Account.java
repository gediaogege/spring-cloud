/*
 *  Copyright 2015-2019 dg-mall.com Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.dg.mall.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * 用户账号实体
 *
 * @author Joe-Benji
 * @date 2019-04-08
 * @since 1.0.0
 */
@Data
public class Account implements Serializable {

    private int id;
    private String accountName;
    private String password;
    private String mobile;
    private String email;
    private short sex;
    private Date birthday;
    private Date createTime;
    private Set<Role> roles;
}

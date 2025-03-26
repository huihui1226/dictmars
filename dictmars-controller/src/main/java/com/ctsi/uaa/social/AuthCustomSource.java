package com.ctsi.uaa.social;

import me.zhyd.oauth.config.AuthSource;

public enum AuthCustomSource implements AuthSource {


    TELECOM {

        @Override
        public String authorize() {
            return "http://134.64.116.90:9072/eam-apps/oauth/authorize";
        }

        /**
         * 获取accessToken的api
         *
         * @return url
         */
        @Override
        public String accessToken() {
            return "http://134.64.116.90:9072/eam-apps/oauth/token";
        }

        /**
         * 获取用户信息的api
         *
         * @return url
         */
        @Override
        public String userInfo() {
            return "http://134.64.116.90:9072/eam-apps/api/v4/user";
        }

    },
    HIAUTH {
        @Override
        public String authorize() {
            return "http://localhost:8181/hiauth/oauth/authorize";
        }

        /**
         * 获取accessToken的api
         *
         * @return url
         */
        @Override
        public String accessToken() {
            return "http://localhost:8181/hiauth/oauth/token";
        }

        /**
         * 获取用户信息的api
         *
         * @return url
         */
        @Override
        public String userInfo() {
            return "http://localhost:8181/hiauth/api/user/profile";
        }
    }

}


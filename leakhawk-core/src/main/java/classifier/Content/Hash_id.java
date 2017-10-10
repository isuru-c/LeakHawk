package classifier.Content;

import org.apache.storm.shade.org.apache.commons.lang.StringUtils;


/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
public class Hash_id {

    String[] hs = {"4607", "3d08", "0e5b","b33fd057"};
    //String[] algorithms = {"CRC16","CRC16CCITT","FCS16","CRC32"};

    /*
    Check if the word is all letters
     */
    private boolean isLetter(String hash){
        return hash.chars().allMatch(Character::isLetter);
    }

    /*
    Check if the word id all digits
     */
    private boolean isAllDigit(String word){
        return word.matches(".*[^0-9].*") || word.matches(".*\\D.*");
    }

    /*
    Check if the word is alphanumeric
     */
    private boolean isAlphaNum(String word){
        return StringUtils.isAlphanumeric(word);
    }

    /*
    Check if the word is a hash
     */
    public boolean isHash(String word) {
        //CRC16
        if(word.length()==hs[0].length() && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //CRC16CCITT
        else if(word.length()==hs[1].length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //FCS16
        else if(word.length()==hs[2].length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //CRC32
        else if(word.length()==hs[3].length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //ADLER32
        else if(word.length()=="0607cb42".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //CRC32B
        else if(word.length()=="b764a0d9".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //XOR32
        else if(word.length()=="0000003f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //GHash323
        else if(word.length()=="80000000".length() && isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //GHash325
        else if(word.length()=="85318985".length() && isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //DESUnix
        else if(word.length()=="ZiY8YtDKXJwYQ".length() && !isAllDigit(word) && !isLetter(word)){
            return true;
        }
        //MD5Half
        else if(word.length()=="ae11fd697ec92c7c".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5Middle
        else if(word.length()=="7ec92c7c98de3fac".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MySQL
        else if(word.length()=="63cea4673fd25f46".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //DomainCachedCredentials
        else if(word.length()=="f42005ec1afe77967cbc83dce1b4d714".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval128
        else if(word.length()=="d6e3ec49aa0f138a619f27609022df10".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval128HMAC
        else if(word.length()=="3ce8b0ffd75bc240fc7d967729cd6637".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD2
        else if(word.length()=="08bbef4754d98806c373f2cd7d9a43c4".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD2HMAC
        else if(word.length()=="4b61b72ead2b0eb0fa3b8a56556a6dca".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD4
        else if(word.length()=="a2acde400e61410e79dacbdfc3413151".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD4HMAC
        else if(word.length()=="6be20b66f2211fe937294c1c95d1cd4f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5
        else if(word.length()=="ae11fd697ec92c7c98de3fac23aba525".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5HMAC
        else if(word.length()=="d57e43d2c7e397bf788f66541d6fdef9".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5HMACWordpress
        else if(word.length()=="3f47886719268dfa83468630948228f6".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //NTLM
        else if(word.length()=="cc348bace876ea440a28ddaeb9fd3550".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RAdminv2x
        else if(word.length()=="baea31c728cbf0cd548476aa687add4b".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD128
        else if(word.length()=="4985351cd74aff0abc5a75a0c8a54115".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD128HMAC
        else if(word.length()=="ae1995b931cf4cbcf1ac6fbf1a83d1d3".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SNEFRU128
        else if(word.length()=="4fb58702b617ac4f7ca87ec77b93da8a".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SNEFRU128HMAC
        else if(word.length()=="59b2b9dcc7a9a7d089cecf1b83520350".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger128
        else if(word.length()=="c086184486ec6388ff81ec9f23528727".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger128HMAC
        else if(word.length()=="c87032009e7c4b2ea27eb6f99723454b".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5passsalt
        else if(word.length()=="5634cc3b922578434d6e9342ff5913f7".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltpasssalt
        else if(word.length()=="469e9cdcaff745460595a7a386c4db0c".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltpassusername
        else if(word.length()=="9ae20f88189f6e3a62711608ddb6f5fd'".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltmd5pass
        else if(word.length()=="aca2a052962b2564027ee62933d2382f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltmd5passsalt
        else if(word.length()=="de0237dc03a8efdf6552fbe7788b2fdd".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltmd5saltpass
        else if(word.length()=="d8f3b3f004d387086aae24326b575b23".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5saltmd5md5passsalt
        else if(word.length()=="81f181454e23319779b03d74d062b1a2".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5username0pass
        else if(word.length()=="e44a60f8f2106492ae16581c91edb3ba".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5usernameLFpass
        else if(word.length()=="654741780db415732eaee12b1b909119".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5usernamemd5passsalt
        else if(word.length()=="954ac5505fd1843bbb97d1b2cda0b98f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5pass
        else if(word.length()=="a96103d267d024583d5565436e52dfb3".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5passsalt
        else if(word.length()=="5848c73c2482d3c2c7b6af134ed8dd89".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5passmd5salt
        else if(word.length()=="8dc71ef37197b2edba02d48c30217b32".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5saltpass
        else if(word.length()=="9032fabd905e273b9ceb1e124631bd67".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5saltmd5pass
        else if(word.length()=="8966f37dbb4aca377a71a9d3d09cd1ac".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5usernamepasssalt
        else if(word.length()=="4319a3befce729b34c3105dbc29d0c40".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5md5pass
        else if(word.length()=="ea086739755920e732d0f4d8c1b6ad8d".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5md5md5pass
        else if(word.length()=="02528c1f2ed8ac7d83fe76f3cf1c133f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5md5md5md5md5pass
        else if(word.length()=="4548d2c062933dff53928fd4ae427fc0".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5sha1pass
        else if(word.length()=="cb4ebaaedfd536d965c452d9569a6b1e".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5sha1md5pass
        else if(word.length()=="099b8a59795e07c334a696a10c0ebce0".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5sha1md5sha1pass
        else if(word.length()=="06e4af76833da7cc138d90602ef80070".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5strtouppermd5pass
        else if(word.length()=="519de146f1a658ab5e5e2aa9b7d2eec8".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //md5strtouppermd5pass
        else if(word.length()=="519de146f1a658ab5e5e2aa9b7d2eec8".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //LineageIIC4
        else if(word.length()=="0x49a57f66bd3d5ba6abda5579c264a0e4".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5Wordpress
        else if(word.length()=="0x49a57f66bd3d5ba6abda5579c264a0e4".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval160
        else if(word.length()=="a106e921284dd69dad06192a4411ec32fce83dbb".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval160HMAC
        else if(word.length()=="29206f83edc1d6c3f680ff11276ec20642881243".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MySQL5
        else if(word.length()=="9bb2fb57063821c762cc009f7584ddae9da431ff".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD160
        else if(word.length()=="dc65552812c66997ea7320ddfb51f5625d74721b".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD160HMAC
        else if(word.length()=="ca28af47653b4f21e96c1235984cb50229331359".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA1
        else if(word.length()=="4a1d4dbc1e193ec3ab2e9213876ceb8f4db72333".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA1HMAC
        else if(word.length()=="6f5daac3fee96ba1382a09b1ba326ca73dccf9e7".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA1MaNGOS
        else if(word.length()=="a2c0cdb6d1ebd1b9f85c6e25e0f8732e88f02f96".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA1MaNGOS2
        else if(word.length()=="644a29679136e09d0bd99dfd9e8c5be84108b5fd".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger160
        else if(word.length()=="c086184486ec6388ff81ec9f235287270429b225".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger160HMAC
        else if(word.length()=="6603161719da5e56e1866e4f61f79496334e6a10".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1passsalt
        else if(word.length()=="f006a1863663c21c541c8d600355abfeeaadb5e4".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1saltpass
        else if(word.length()=="299c3d65a0dcab1fc38421783d64d0ecf4113448".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1saltmd5pass
        else if(word.length()=="860465ede0625deebb4fbbedcb0db9dc65faec30".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1saltmd5passsalt
        else if(word.length()=="6716d047c98c25a9c2cc54ee6134c73e6315a0ff".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1saltsha1pass
        else if(word.length()=="58714327f9407097c64032a2fd5bff3a260cb85f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1saltsha1saltsha1pass
        else if(word.length()=="cc600a2903130c945aa178396910135cc7f93c63".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1usernamepass
        else if(word.length()=="3de3d8093bf04b8eb5f595bc2da3f37358522c9f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1usernamepasssalt
        else if(word.length()=="00025111b3c4d0ac1635558ce2393f77e94770c5".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1md5pass
        else if(word.length()=="fa960056c0dea57de94776d3759fb555a15cae87".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1md5passsalt
        else if(word.length()=="1dad2b71432d83312e61d25aeb627593295bcc9a".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1md5sha1pass
        else if(word.length()=="8bceaeed74c17571c15cdb9494e992db3c263695".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1sha1passsalt
        else if(word.length()=="3109b810188fcde0900f9907d2ebcaa10277d10e".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1sha1pass
        else if(word.length()=="780d43fa11693b61875321b6b54905ee488d7760".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1sha1passsubstrpass03
        else if(word.length()=="5ed6bc680b59c580db4a38df307bd4621759324e".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1sha1saltpass
        else if(word.length()=="70506bac605485b4143ca114cbd4a3580d76a413".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1sha1sha1pass
        else if(word.length()=="3328ee2a3b4bf41805bd6aab8e894a992fa91549".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //sha1strtolowerusernamepass
        else if(word.length()=="79f575543061e158c2da3799f999eb7c95261f07".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval192
        else if(word.length()=="cd3a90a3bebd3fa6b6797eba5dab8441f16a7dfa96c6e641".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval192
        else if(word.length()=="39b4d8ecf70534e2fd86bb04a877d01dbf9387e640366029".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger192
        else if(word.length()=="c086184486ec6388ff81ec9f235287270429b2253b248a70".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Tiger192HMAC
        else if(word.length()=="8e914bb64353d4d29ab680e693272d0bd38023afa3943a41".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5passsaltjoomla1
        else if(word.length()=="35d1c0d69a2df62be2df13b087343dc9:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA1Django
        else if(word.length()=="sha1$Zion3R$299c3d65a0dcab1fc38421783d64d0ecf4113448:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval224
        else if(word.length()=="f65d3c0ef6c56f4c74ea884815414c24dbf0195635b550f47eac651a:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval224HMAC
        else if(word.length()=="f10de2518a9f7aed5cf09b455112114d18487f0c894e349c3c76a681:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA224
        else if(word.length()=="e301f414993d5ec2bd1d780688d37fe41512f8b57f6923d054ef8e59:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA224HMAC
        else if(word.length()=="c15ff86a859892b5e95cdfd50af17d05268824a6c9caaa54e4bf1514".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256
        else if(word.length()=="2c740d20dab7f14ec30510a11f8fd78b82bc3a711abe8a993acdb323e78e6d5e:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256HMAC
        else if(word.length()=="d3dd251b7668b8b6c12e639c681e88f2c9b81105ef41caccb25fcde7673a1132:BeKMviAfcXeTPTlX".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval256
        else if(word.length()=="7169ecae19a5cd729f6e9574228b8b3c91699175324e6222dec569d4281d4a4a".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Haval256HMAC
        else if(word.length()=="6aa856a2cfd349fb4ee781749d2d92a1ba2d38866e337a4a1db907654d4d4d7a".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //GOSTR341194
        else if(word.length()=="ab709d384cce5fda0793becd3da0cb6a926c86a8f3460efb471adddee1c63793".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD256
        else if(word.length()=="5fcbe06df20ce8ee16e92542e591bdea706fbdc2442aecbf42c223f4461a12af".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD256HMAC
        else if(word.length()=="43227322be1b8d743e004c628e0042184f1288f27c13155412f08beeee0e54bf".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SNEFRU256
        else if(word.length()=="3a654de48e8d6b669258b2d33fe6fb179356083eed6ff67e27c5ebfa4d9732bb".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SNEFRU256HMAC
        else if(word.length()=="4e9418436e301a488f675c9508a2d518d8f8f99e966136f2dd7e308b194d74f9".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256md5pass
        else if(word.length()=="b419557099cfa18a86d1d693e2b3b3e979e7a5aba361d9c4ec585a1a70c7bde4".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256sha1pass
        else if(word.length()=="afbed6e0c79338dbfe0000efe6b8e74e3b7121fe73c383ae22f5b505cb39c886".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //MD5passsaltjoomla2
        else if(word.length()=="fb33e01e4f8787dc8beb93dac4107209:fxJUXVjYRafVauT77Cze8XwFrWaeAYB2".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SAM
        else if(word.length()=="4318B176C3D8E3DEAAD3B435B51404EE:B7C899154197E8A2A33121D76A240AB5".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256Django
        else if(word.length()=="sha256$Zion3R$9e1a08aa28a22dfff722fad7517bae68a55444bb5e2f909d340767cec9acf2c3".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD320
        else if(word.length()=="b4f7c8993a389eac4f421b9b3b2bfb3a241d05949324a8dab1286069a18de69aaf5ecc3c2009d8ef".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //RipeMD320HMAC
        else if(word.length()=="244516688f8ad7dd625836c0d0bfc3a888854f7c0161f01de81351f61e98807dcd55b39ffe5d7a78".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA384
        else if(word.length()=="3b21c44f8d830fa55ee9328a7713c6aad548fe6d7a4a438723a0da67c48c485220081a2fbc3e8c17fd9bd65f8d4b4e6b".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA384HMAC
        else if(word.length()=="bef0dd791e814d28b4115eb6924a10beb53da47d463171fe8e63f68207521a4171219bb91d0580bca37b0f96fddeeb8b".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA256s
        else if(word.length()=="$6$g4TpUQzk$OmsZBJFwvy6MwZckPvVYfDnwsgktm2CckOlNJGy9HNwHSuHFvywGIuwkJ6Bjn3kKbB6zoyEjIYNMpHWBNxJ6g.".length() && !isAllDigit(word) && !isLetter(word) && !isAlphaNum(word)){
            return true;
        }
        //SHA384Django
        else if(word.length()=="sha384$Zion3R$88cfd5bc332a4af9f09aa33a1593f24eddc01de00b84395765193c3887f4deac46dc723ac14ddeb4d3a9b958816b7bba".length() && !isAllDigit(word) && !isLetter(word) && !isAlphaNum(word)){
            return true;
        }
        //SHA512
        else if(word.length()=="ea8e6f0935b34e2e6573b89c0856c81b831ef2cadfdee9f44eb9aa0955155ba5e8dd97f85c73f030666846773c91404fb0e12fb38936c56f8cf38a33ac89a24e".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //SHA512HMAC
        else if(word.length()=="dd0ada8693250b31d9f44f3ec2d4a106003a6ce67eaa92e384b356d1b4ef6d66a818d47c1f3a2c6e8a9a9b9bdbd28d485e06161ccd0f528c8bbb5541c3fef36f".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //Whirlpool
        else if(word.length()=="76df96157e632410998ad7f823d82930f79a96578acc8ac5ce1bfc34346cf64b4610aefa8a549da3f0c1da36dad314927cebf8ca6f3fcd0649d363c5a370dddb".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        //WhirlpoolHMAC
        else if(word.length()=="77996016cf6111e97d6ad31484bab1bf7de7b7ee64aebbc243e650a75a2f9256cef104e504d3cf29405888fca5a231fcac85d36cd614b1d52fce850b53ddf7f9".length() && !isAllDigit(word) && !isLetter(word) && isAlphaNum(word)){
            return true;
        }
        return false;
    }

}


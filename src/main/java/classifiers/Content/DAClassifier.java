/*
 * Copyright 2017 SWIS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package classifiers.Content;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sugeesh Chandraweera
 */
@SuppressWarnings("ALL")
public class DAClassifier extends ContentClassifier {

    ArrayList<Pattern> unigramPatternList;
    ArrayList<Pattern> bigramPatternList;
    ArrayList<Pattern> trigramPatternList;

    Pattern relatedPattern1;
    Pattern relatedPattern2;
    Pattern relatedPattern3;
    Pattern relatedPattern4;
    Pattern relatedPattern5;
    Pattern relatedPattern6;
    Pattern relatedPattern7;

    public DAClassifier() {
        ArrayList<String> unigramList = new ArrayList<String>();
        unigramList.add("MX|NS|PTR|CNAME|SOA");
        unigramList.add("dns|record|host");
        unigramList.add("INTO");
        unigramList.add("snoop|axfr|brute|poisoning");

        ArrayList<String> bigramList = new ArrayList<String>();
        bigramList.add("43200 IN|10800 IN|86400 IN|3600 IN");
        bigramList.add("IN A|IN MX|IN NS|IN CNAME");
        bigramList.add("no PTR");
        bigramList.add("hostnames found");
        bigramList.add("zone transfer");
        bigramList.add("MX 10|MX 20|MX 30|MX 40|MX 50|MX 60");

        ArrayList<String> trigramList = new ArrayList<String>();
        trigramList.add("transfer not allowed");
        trigramList.add("Trying zone transfer");


        unigramPatternList = new ArrayList<Pattern>();
        for (String word : unigramList) {
                unigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));

        }

        bigramPatternList = new ArrayList<Pattern>();
        for (String word : bigramList) {
            if(word.equals("MX 10|MX 20|MX 30|MX 40|MX 50|MX 60")) {
                bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
            }else {
                bigramPatternList.add(getCorrectPatten("\\b" + word + "\\b"));
            }
        }

        trigramPatternList = new ArrayList<Pattern>();
        for (String word : trigramList) {
            if(word.equals("Trying zone transfer")) {
                trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE));
            }else {
                trigramPatternList.add(getCorrectPatten("\\b" + word + "\\b"));
            }

        }

        relatedPattern1 = getCorrectPatten("\\b" + "dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern2 = Pattern.compile("dns-brute|dnsrecon|fierce|tsunami|Dnsdict6|axfr", Pattern.CASE_INSENSITIVE);
        relatedPattern3 = getCorrectPatten("\\b" + "DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern4 = Pattern.compile("DNS LeAkEd|DNS fuck3d|zone transfer|DNS_Enumeration|Enumeration_Attack", Pattern.CASE_INSENSITIVE);
        relatedPattern5 = getCorrectPatten("\\b" + "DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping" + "\\b", Pattern.CASE_INSENSITIVE);
        relatedPattern6 = Pattern.compile("DNS Enumeration Attack|DNS enumeration|zone transfer|misconfigured DNS|DNS Cache Snooping", Pattern.CASE_INSENSITIVE);
        relatedPattern7 = Pattern.compile("\\b" +"\\[\\*\\]"+"\\b", Pattern.CASE_INSENSITIVE);
    }

    /*public static void main(String[] args) {
        DAClassifier daClassifier = new DAClassifier();
        System.out.println("Result is :" + daClassifier.classify("//require modules\n" +
                "import \"babel-core/register\"\n" +
                "import \"babel-polyfill\"\n" +
                "import Vue from 'vue'\n" +
                "import VueRouter from 'vue-router'\n" +
                "import notify from 'v-toaster'\n" +
                "import Vuetify from 'vuetify'\n" +
                "import VueCookies from 'vue-cookies'\n" +
                "import Store from './store/index'\n" +
                "import 'v-toaster/dist/v-toaster.css'\n" +
                "\n" +
                "// require components\n" +
                "import Index from '../public/components/App.vue'\n" +
                "import Authorization from './components/Authorization.vue'\n" +
                "import Dashboard from './components/Dashboard.vue'\n" +
                "\n" +
                "const routes = [\n" +
                "    { path: '/', component: Index},\n" +
                "    { path: '/signin', component: Authorization},\n" +
                "    { path: '/dashboard', component: Dashboard},\n" +
                "];\n" +
                "\n" +
                "\n" +
                "//Setup Vue\n" +
                "Vue.use(VueRouter);\n" +
                "Vue.use(notify, {timeout: 5000});\n" +
                "Vue.use(Vuetify);\n" +
                "Vue.use(VueCookies);\n" +
                "\n" +
                "const router = new VueRouter({\n" +
                "    mode: 'history',\n" +
                "    routes: routes\n" +
                "});\n" +
                "\n" +
                "\n" +
                "new Vue({\n" +
                "    el: '#app',\n" +
                "    router: router,\n" +
                "    store: Store\n" +
                "    //render: h => h(Index)\n" +
                "\n" +
                "\n" +
                "});\n" +
                "\t\n" +
                "import Vue from 'vue'\n" +
                "import Vuex from 'vuex'\n" +
                "import axios from 'axios'\n" +
                "\n" +
                "Vue.use(Vuex);\n" +
                "\n" +
                "const store = new Vuex.Store({\n" +
                "    state: {\n" +
                "        login: false,\n" +
                "\n" +
                "    },\n" +
                "\n" +
                "    getters: {\n" +
                "        login(state) {\n" +
                "            return state.login;\n" +
                "        },\n" +
                "    },\n" +
                "\n" +
                "    mutations: {\n" +
                "        login(state, {type, value}) {\n" +
                "            state[type] = value;\n" +
                "        },\n" +
                "    },\n" +
                "\n" +
                "    actions: {\n" +
                "\n" +
                "        async checkLogin ({state, commit}) {\n" +
                "            let res = await axios.post('/checkLogin');\n" +
                "\n" +
                "            const login = res.data;\n" +
                "\n" +
                "            login ? this.$toaster.info(\"Welcome\") : this.$toaster.info(\"Not authorized\");\n" +
                "\n" +
                "            commit('login', {type: 'login', value: login});\n" +
                "        },\n" +
                "    },\n" +
                "\n" +
                "\n" +
                "\n" +
                "});\n" +
                "\t\n" +
                "<script>\n" +
                "    import axios from 'axios';\n" +
                "\n" +
                "\n" +
                "    export default {\n" +
                "        data() {\n" +
                "            return {\n" +
                "\n" +
                "                email: null,\n" +
                "                path: '/',\n" +
                "                errors: [],\n" +
                "                rules: [],\n" +
                "            }\n" +
                "        },\n" +
                "\n" +
                "        computed: {\n" +
                "            login() {\n" +
                "                let login = this.$store.getters.login;\n" +
                "\n" +
                "                if(!login)\n" +
                "                    this.$toaster.error(\"Unauthorized\");\n" +
                "                else if ( login )\n" +
                "                    this.$router.push('/dashboard');\n" +
                "                return login;\n" +
                "            }\n" +
                "        },\n" +
                "\n" +
                "        methods: {\n" +
                "\n" +
                "            SignIn: async function () {\n" +
                "                let data = {email: this.email, password: this.password};\n" +
                "\n" +
                "                try {\n" +
                "                    let signin = await axios.post('/login', data);\n" +
                "\n" +
                "                    this.setLogin(signin.data);\n" +
                "\n" +
                "                } catch(e) {\n" +
                "                    this.errorHandler(e);\n" +
                "                }\n" +
                "            },\n" +
                "\n" +
                "            setLogin: function (value) {\n" +
                "                this.$store.commit('login', {type: 'login', value: login});\n" +
                "            },\n" +
                "\n" +
                "\n" +
                "        }\n" +
                "    }\n" +
                "</script>\n" +
                "\t\n" +
                "computed: {\n" +
                "     login() {\n" +
                "         let login = this.$store.getters.login;\n" +
                "\n" +
                "         if(!login)\n" +
                "             this.$toaster.error(\"Unauthorized\");\n" +
                "                 else if ( login )\n" +
                "             this.$router.push('/dashboard');\n" +
                "                    return login;\n" +
                "     }\n" +
                "},", "1.txt","asas"));
    }*/


    @Override
    public String createARFF(String text,String title) {
        String feature_list = "";

        for (Pattern pattern : unigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : bigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        for (Pattern pattern : trigramPatternList) {
            Matcher matcher = pattern.matcher(text);
            feature_list += getMatchingCount(matcher) + ",";
        }

        Matcher matcher = relatedPattern1.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern2.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern3.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern4.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern5.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern6.matcher(title);
        feature_list += getMatchingCount(matcher) + ",";

        matcher = relatedPattern7.matcher(text);
        feature_list += getMatchingCount(matcher) + ",";

        feature_list += "?";
        return headingDA+feature_list;
    }

    @Override
    public boolean classify(String text,String title) {
        try{
            // convert String into InputStream
            String result = createARFF(text,title);
            InputStream is = new ByteArrayInputStream(result.getBytes());

            // read it with BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // BufferedReader reader = new BufferedReader
            Instances unlabeled = new Instances(reader);
            reader.close();
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // create copy
            Instances labeled = new Instances(unlabeled);

            RandomForest tclassifier = (RandomForest) weka.core.SerializationHelper.read("./src/main/resources/DA.model");
            String[] options = new String[2];
            options[0] = "-P";
            options[1] = "0";
            tclassifier.setOptions(options);

            double pred = tclassifier.classifyInstance(unlabeled.instance(0));
            String classLabel = unlabeled.classAttribute().value((int) pred);

            if("DA".equals(classLabel)){
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*@Override
    public boolean classify(String text, String title,String key) {
        try {
            String result = createARFF(text, title);

            BufferedWriter bw = null;
            FileWriter fw = null;
            try {
                fw = new FileWriter("./src/main/java/classifiers/Content/arff/da" + key + ".arff");
                bw = new BufferedWriter(fw);
                bw.write(result);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                    if (fw != null)
                        fw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            ProcessBuilder pbVal = new ProcessBuilder("/bin/bash", "/home/neo/Desktop/FinalYearProject/LeakHawk/src/main/java/classifiers/Content/validator/DA_validator.sh", "./src/main/java/classifiers/Content/arff/da" + key + ".arff");
            final Process processVal = pbVal.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(processVal.getInputStream()));
            String line = br.readLine();
            if(line!=null) {
                if (line.contains("non")) {
                    return false;
                } else if (line.contains("DA")) {
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            File file = new File("./src/main/java/classifiers/Content/arff/da" + key + ".arff");
            file.delete();
        }
        return false;
    }*/

}

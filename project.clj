(defproject travelmule "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :source-paths ["src/clj" "src/cljs" "src/cljc"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring-server "0.4.0"]
                 [cljsjs/react "0.13.3-1"]
                 [cljs-ajax "0.3.14"]
                 [reagent "0.5.0"]
                 [reagent-forms "0.5.5"]
                 [reagent-utils "0.1.5"]
                 [org.clojure/clojurescript "1.7.28"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [org.clojure/data.json "0.2.6"]
                 [com.fasterxml.jackson.dataformat/jackson-dataformat-cbor "2.6.0"]
                 [ring/ring-json "0.4.0"]
                 [buddy "0.6.0"]
                 [com.novemberain/validateur "2.4.2"]
                 [yesql "0.4.2"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ragtime "0.5.0"]
                 [prone "0.8.2"]
                 [compojure "1.4.0"]
                 [selmer "0.8.7"]
                 [environ "1.0.0"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-environ "1.0.0"]
            [lein-ring "0.9.1"]
            [lein-asset-minifier "0.2.2"]
            [lein-midje "3.1.3"]]

  :ring {:handler travelmule.handler/app
         :init travelmule.handler/init
         :uberwar-name "travelmule.war"}

  :min-lein-version "2.5.0"

  :uberjar-name "travelmule.jar"

  :main travelmule.server

  :clean-targets ^{:protect false} ["resources/public/js"]

  :minify-assets
  {:assets
    {"resources/public/css/site.min.css" "resources/public/css/site.css"}}

  :cljsbuild {:builds {:app {:source-paths ["src/cljs" "src/cljc"]
                             :compiler {:output-to     "resources/public/js/app.js"
                                        :output-dir    "resources/public/js/out"
                                        :asset-path   "js/out"
                                        :optimizations :none
                                        :pretty-print  true}}}}

  :profiles {:dev {:repl-options {:init-ns travelmule.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.4.0"]
                                  [leiningen "2.5.1"]
                                  [figwheel "0.3.7"]
                                  [weasel "0.7.0"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [pjstadig/humane-test-output "0.7.0"]
                                  [midje "1.7.0"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.2.5"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :figwheel {:http-server-root "public"
                              :server-port 3449
                              :css-dirs ["resources/public/css"]
                              :ring-handler travelmule.handler/app}

                   :env {:dev? true}

                   :cljsbuild {:builds {:app {:source-paths ["env/dev/cljs"]
                                              :compiler {:main "travelmule.dev"
                                                         :source-map true}}
                                        }
                               }}

             :uberjar {:hooks [leiningen.cljsbuild minify-assets.plugin/hooks]
                       :env {:production true}
                       :aot :all
                       :omit-source true
                       :cljsbuild {:jar true
                                   :builds {:app
                                             {:source-paths ["env/prod/cljs"]
                                              :compiler
                                              {:optimizations :advanced
                                               :pretty-print false}}}}}})

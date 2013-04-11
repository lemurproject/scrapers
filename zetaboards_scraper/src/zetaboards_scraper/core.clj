; Scrape zetaboards and shit
 
(ns zetaboards-scraper.core
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html])
  (:use [clojure.string :only (join split)]))


(defn get-forum-links
  [page-src-stream]
  (map #(:href (:attrs %))
       (html/select (html/html-resource page-src-stream) [:dl :dt :a])))

(defn -main
  [& args]
  (let [[args-vector [warc-file-name] banner] (cli/cli args ["--get-forum-links" :default false])]
    (println args-vector)
    (when (:get-forum-links args-vector)
    	(doseq [record (warc/get-http-records-seq (warc/get-warc-reader warc-file-name))]
      		(doseq [forum-link (get-forum-links (io/reader (:payload-stream record)))]
          		(println forum-link))))))
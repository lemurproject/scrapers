(ns proboards-scraper.core
  (:gen-class :main true)
  (:require [warc-clojure.core :as warc]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]))

(defn extract-forum-links
  [page-html]
  (filter #(. (str %) startsWith "http")
          (map #(:href (:attrs %)) (html/select page-html [:ul.link_list :li :a]))))

(defn -main
  [& args]
  (let [[args-map [warc-file-name] banner] (cli/cli args)]
    (doseq [record (warc/get-http-records-seq (warc/get-warc-reader warc-file-name))]
      		(doseq [link (extract-forum-links (html/html-resource (io/reader (:payload-stream record))))]
          			(println link)))))

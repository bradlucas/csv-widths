(ns csv-widths.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [cli parse-opts]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as string]))

;; read file line by line
;; map of max column widths
;;
;; {
;;  1 256
;;  2 18
;;  3 256
;;  4 3
;; }


;; Separators
;;
;; ,
;; |
;; \t
;;
;; lein run -f data.csv -s $'\t'
;; lein run -f data.csv -s $'|'
;; lein run -f data.csv -s ,

(def cli-options
  [["-f" "--filename FILENAME"]
   ["-s" "--separator CHAR" :default ","]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> [""
        "Usage: csv-widths"
        ""
        "Options:"
        options-summary
        ""
        "Examples:"
        " csv-widths -f data.csv"
        " csv-widths -f data.csv -s ,"
        " csv-widths -f data.csv $'\t"
        " csv-widths -f data.csv $'|'"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))


(defn calc
  [row]
  ;; (map-indexed vector (map count ["cae89142-858d-4fd7-80e6-d56b68f59fce"  "633820301408851"  "EC14F53C-CFFF-432B-812D-4FC6DF4B61A1"  "HGT"]))

  ;; @see http://stackoverflow.com/a/21967586
  (let [indexed-map (map-indexed (fn [i v] (vector (inc i) v))  (map count row))]
    ;; turn into map
    (into {} indexed-map)
    ;; indexed-map
    )
)
  
(defn merge-map
  [a b]
  (merge-with max a b))

(defn print-results
  [m]
  (doseq [[k v] m] (println (format "Column %d width %d" k v))))


(defn process-file
  [fname separator]
  (let [sep (if separator (char (first separator)) \,)]
    (println fname separator)
    (with-open [f (io/reader fname)]
      ;; (map #(fn [%] (println (calc %))) (doall (line-seq in-file)))
      ;; (doall (map #(fn [%] (println (calc %))) (line-seq in-file)))
      (doall (reduce merge-map {} (map calc (csv/read-csv f :separator sep))))
      ;; csv/read-csv
      )
    )
  )

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (println options)
    (cond
     (:filename options) (println (process-file (:filename options) (:separator options)))
     (:help options) (exit 0 (usage summary))
     errors (exit 1 (error-msg errors))
     true (exit 0 (usage summary))
     )
    )
)

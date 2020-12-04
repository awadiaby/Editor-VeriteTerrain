# -*- coding: utf-8 -*-
import time
import os 
import html2text
import Extractor_python_html
from Extractor_python_html import Extractor_python_html
from datetime import datetime, timedelta

#####################
# global variables
#####################

BASE_WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/"
h2txt = html2text.HTML2Text()


##################################
#
# MAIN
#
##################################
# Load urls in variable
extractor_python_html = Extractor_python_html()
if __name__ == '__main__':
    start_time = time.monotonic() # beginning time
    extractor_python_html.extract()
    end_time = time.monotonic()   # end time
    timeexec= (end_time - start_time)*1000 # execution time in milliseconde
    # count number of csv extract
    list = os.listdir('output/csv') 
    number_files = len(list)
    print ("Number of extracted CSV: ",number_files," lines")
    lignesHtml =  open("number-execution.txt", "w")
    lignesHtml.write(str(number_files))
    print('Duration:', timeexec," millisecondes")
    lignesHtml =  open("temps-execution.txt", "w")
    lignesHtml.write(str(timeexec))
    print("All done !")
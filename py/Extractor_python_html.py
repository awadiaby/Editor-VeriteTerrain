# install some libs
#!pip install requests
#!pip install html2text

#####################
# imports
#####################
import requests
import html2text
from bs4 import BeautifulSoup

class Extractor_python_html:
    #####################
    # global variables
    #####################
    BASE_WIKIPEDIA_URL = "https://en.wikipedia.org/wiki/"
    h2txt = html2text.HTML2Text()
    
    #####################
    # Functions
    #####################
    
    # Load input file to array
    def load_file_content(self,file):
        with open("inputdata/{}".format(file), 'r', encoding = 'utf-8') as f:
            data = f.readlines()
            
    
        return data
    
    
    # Return html content from url and None if smthg wrong happened
    def read_url_html_content(self,url):
    
        url = "{}{}".format(self.BASE_WIKIPEDIA_URL, url) # build real URL
    
        try:
            response = requests.get(url)
            response.raise_for_status()
            #print('****************response', response.status_code)
            return response.text
        except requests.exceptions.HTTPError as errh:
            print ("Http error : ",errh)
        except requests.exceptions.ConnectionError as errc:
            print ("Error connecting : ",errc)
        except requests.exceptions.Timeout as errt:
            print ("Timeout error : ",errt)
        except requests.exceptions.RequestException as err:
            print ("Oops ! Something wrong : ",err)
    
        return None
    
    
    # Read HTML content and capture all <table> tags in some array
    def get_tables_from_html(self, html_content):
        
        soup = BeautifulSoup(html_content, "html.parser")
        wikitables = soup.find_all('table', class_ = 'wikitable') # get all table tag with wikitable class
        
        #wikitables = [table for table in tables if table.get('class') is not None and ("wikitable" in table.get('class'))]
        return wikitables
    
    
    # Clean a given table cell (td or th) content from html to text
    def get_cleaned_table_cell(self,html_cell):
        temp_txt = self.h2txt.handle(str(html_cell)) # make sure it a str param
        soup = BeautifulSoup(temp_txt, 'html.parser')
        cleaned_content = soup.get_text().replace("\n", "")
        return cleaned_content
    
    
    # input data : table_tag <table>........</table>
    # return : equivalent table as csv
    def read_table_tag_to_csv(self,wikitable):
    
        csv = ''
        soup = BeautifulSoup(str(wikitable), "html.parser")
    
        lines = soup.tbody.find_all('tr')
    
        for line in lines:
    
            new_line = ''
            for child in line: # for each column (td, th, ...) tags
                
                cell_content = str(child).replace("\n", "")
    
                if cell_content and ("<" in cell_content): # cell not empty and is and HTML tag
    
                    cleaned_content = self.get_cleaned_table_cell(str(child).replace("\n", ""))
                    new_line += '"{}";'.format(cleaned_content)
    
            new_line = new_line[0:-1] # remove last ';' added
            csv += '{}\n'.format(new_line)
    
        csv = csv[0:-1] # remove last \n char added
    
        return csv
    
    
    # Write csv content to csv directory with name
    def output_csv_file(self,name, content):
        with open('output/csv/{}.csv'.format(name), 'w',  encoding = 'utf-8') as handler:
            handler.write(content)
    
    # Write csv content to csv directory with name
    def output_csv_file2(self,name, content, outputFolderName):
        with open('output/' + outputFolderName + '/{}.csv'.format(name), 'w',  encoding = 'utf-8') as handler:
            handler.write(content)
    
    # Write html content to html directory with name
    def output_html_file(self, name, content):
        with open('output/html/{}.html'.format(name), 'w',  encoding = 'utf-8') as handler:
            handler.write(content)


    def extract_wikitables(self,wikiurl):

    # read HTML content from url
        wiki_html_content = self.read_url_html_content(url=wikiurl)

        if wiki_html_content is not None: # everything went fine

        # save html file
            self.output_html_file(name=wikiurl, content=wiki_html_content)

        # get tables in html content
            wikitables = self.get_tables_from_html(wiki_html_content)

            i = 0
            for wikitable in wikitables:
                
               i += 1
               try:
                    wikicsv = self.read_table_tag_to_csv(wikitable=wikitable)  #read table to csv
                    self.output_csv_file(name="{}_{}".format(wikiurl, i), content=wikicsv) #save it to file
               except Exception as e:
                   print("Something went wrong on table : {} \nError : {}".format(i, e))
                   return False, 0
        else:
           return False, 0

        return True, i



    def extract(self):
    # Load urls in variable
       wikiurls = self.load_file_content("wikiurls.txt")

       n = 0
       for wikiurl in wikiurls:
        n += 1
        print("Loading url {} ...".format(n))
        wikiurl = wikiurl[0:-1]
        self.extract_wikitables(wikiurl=wikiurl)

        






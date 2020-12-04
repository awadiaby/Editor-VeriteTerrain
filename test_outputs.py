from os import listdir
from os.path import isfile, join
from csv import reader

OUTPUT_JAVA_HTML = 'JavaExtractor/output/html'
OUTPUT_JAVA_WIKITEXT = 'JavaExtractor/output/wikitext'
OUTPUT_PYTHON = 'PyExtractor/output/csv'
OUTPUT_VERITE = 'src/verite'


######################
# Test Java HTML
######################

# Parcourir les fichiers de verite
#
# Verifier s'il y a son correspondant dans java html
# non  => le test a échoué pour cette table de cette url
# oui =>
#       Verifier si les contenus ligne par ligne des fichiers sont équivalents
#       non => le test a échoué pour cette table de cette url
#       oui => OK


def file_content_to_table(filename):
    table = list()
    with open(filename, 'r', encoding='utf-8', errors='ignore') as handler:
        csv_reader = reader(handler)
        for row in csv_reader:
            table.append(row)

    return table


def test_verite_on_file(output_dir, output_dir_files, truth_file):

    test_result = True
    verite_file_lines = file_content_to_table(OUTPUT_VERITE + '/' + truth_file)

    # Java HTML
    if truth_file in output_dir_files:

        #read file line by line
        output_file_lines = file_content_to_table(
            output_dir + '/' + truth_file)

        for (i, line) in enumerate(verite_file_lines):
            
            line = str(line).replace("\"", "")
            line = str(line).replace("\n", "")

            if i >= len(output_file_lines) or str(line) != output_file_lines[i]:  # la même ligne
                test_result = False

    else:
        test_result = False

    return test_result


# ====================================================================

truth_files = listdir(OUTPUT_VERITE)
output_java_html = listdir(OUTPUT_JAVA_HTML)
output_java_wikitext = listdir(OUTPUT_JAVA_WIKITEXT)
output_python = listdir(OUTPUT_PYTHON)

# pour pourcentage
percent_total = len(truth_files)
java_html_success = 0
java_wikitext_success = 0
python_success = 0


for truth_file in truth_files:

    javahtml_test_result = test_verite_on_file(output_dir=OUTPUT_JAVA_HTML, output_dir_files=output_java_html, truth_file=truth_file)
    javawiki_test_result = test_verite_on_file(output_dir=OUTPUT_JAVA_WIKITEXT, output_dir_files=output_java_wikitext, truth_file=truth_file)
    python_test_result = test_verite_on_file(output_dir=OUTPUT_PYTHON, output_dir_files=output_python, truth_file=truth_file)

    if javahtml_test_result:
        java_html_success += 1

    if javawiki_test_result:
        java_wikitext_success += 1

    if python_test_result:
        python_success += 1

print("Python Extractor : {} % succes".format(python_success / percent_total))
print("Java Html Extractor : {} % succes".format(java_html_success / percent_total))
print("Java wiki Extractor : {} % succes".format(java_wikitext_success / percent_total))
           
def is_it_question(sIn):
    return sIn.endswith('?') or sIn.endswith(':')

def clear_special_symbols(in_file, out_file):
    special_chars = ['~', '=', '#', '{', '}', ':']
    for line in in_file:
        for c in special_chars:
            line = line.replace(c, '\\' + c)
        out_file.write(line)

def format_answers(step1_file, out_file):
    single_answer_questions_counter = 0
    multiple_answer_questions_counter = 0
    buf_question = None

    for sIn in step1_file:
        sIn = sIn.strip()

        if is_it_question(sIn):
            if buf_question is not None:
                out_file.write("}\n\n")
            buf_question = sIn
            buf_question_number = 0
            single_answer_questions_counter += 1

            out_file.write("::Вопрос " + str(single_answer_questions_counter) + "::" + buf_question + "{\n")
        else:
            if buf_question is None:
                continue

            buf_question_number += 1
            if sIn.startswith('+'):
                sIn = sIn[1:]
                out_file.write("=" + sIn + "\n")
            else:
                out_file.write("~" + sIn + "\n")

    if buf_question is not None:
        out_file.write("}\n\n")

    print("Step 2 is complete:")
    print("Questions found overall:", single_answer_questions_counter)
    print("1-answer questions found:", single_answer_questions_counter)
    print("mult-answer questions found:", multiple_answer_questions_counter)

if __name__ == "__main__":
    with open("textIn.txt", "r") as fIn, open("step1_clear_special_symbols.txt", "w") as fStep1, open("giftOut.txt", "w") as fOut:
        clear_special_symbols(fIn, fStep1)
        fStep1.close()
        fStep1 = open("step1_clear_special_symbols.txt", "r")
        format_answers(fStep1, fOut)

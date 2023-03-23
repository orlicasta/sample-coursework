courseNames = ("Computer Security", "Numerical Analysis", "Senior Capstone", "Prog Language Design")
instructors = ("Chen", "Hajiarbabi", "Kim", "Khalifa")
scores = (100, 90, 80, 70)

#by index n-1
print(instructors[3])

#by unpacking
a, b, c, d = courseNames
print(d)

#by len() method
print(scores.__len__())

#by in operator
print("Numerical Analysis" in courseNames)

#two tuples are concatenated with + operator
#resulting in a single new tuple with length 8
print(scores + instructors)
print((scores + instructors).__len__())

#each tuple is a list item
jacobSemester = [courseNames, instructors, scores]
#iterate over each tuple element in each list item
#using nested for loops
for item in jacobSemester:
    for element in item:
        print(element)
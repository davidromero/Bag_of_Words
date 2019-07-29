## Description
Bag of words classification project using a Naive Bayes probabilistic model.
The program takes a text file to train itself, counting every word associated with the tag and it calculates the probability of an outcome given a word.

## Quickstart

![alt text](https://user-images.githubusercontent.com/10179447/50039652-44be5a80-fffb-11e8-91a3-6d266bfc5df3.png "Fresh program")

To use the program, select a text file with the 'Choose training file' button and with the following structure and hit 'Run':
```
rainy    hot    high    false   | no
rainy    hot    high    true    | no
overcast hot    high    false   | yes
sunny    mild   high    false   | yes

```
The left side represents words associated with the right side word.

When the program has calculated all the words probability the results will be available at the left side of the window.
![alt text](https://user-images.githubusercontent.com/10179447/50039653-44be5a80-fffb-11e8-8e28-7ac749fefbe6.png "Trained program")

Finally you can enter a phrase and click 'Clasify' to get the most likely outcome with some additional information.
![alt text](https://user-images.githubusercontent.com/10179447/50039654-44be5a80-fffb-11e8-9600-13385a856173.png "Program result")

After each classification the program will change it's probability by saving the words of the phrase to the training set.
The calculations use Laplace smoothing of a value of 1.

#TM:Design Doc
   
John Pettet
---
###Log:  
The log class works by serializing a linked List that is the actual log. This linked list holds a private inner class Record. The advantage of this approach is the elimination of any need to parse strings from the log file. The Log class provides only a handful of methods. One for adding to the log, another for receiving a list of all tasks represented in the log. There are two methods for querying data from the log. One returns the most recent instance of a (command,task) pair, while the other returns all such instances. 

One drawback of the Log as it stands is that entries are ordered based on when they were last worked on. This could be desirable as when all tasks are summarized they will naturally be listed in order from most recent to least recent. However if a different sorting were desired that cannot currently be easily performed. Another drawback is currently there is no way for the user to specify the location for storage of the log file. The log file also cannot be renamed. 

These drawbacks are not so severe for our intended user. For a single person working on only a few projects there should be no need to alter the log file location. The lack of ability to sort the entries any particular way should not hinder usage given the small number of projects an individual is predicted to be working on.

###TM:  
The TM class is executable from the command line only. This is fine for our expected customer of programmers but would generalize poorly to the general population. It also has a somewhat picky syntax, it expects the description in the ```TM describe <task> <description>``` command for instance must be surrounded in quotes e.g. ```TM describe foo "the description"```. This is fine for programmers however the general population may find this confusing or difficult to remember. 

The TM now associates all describe commands in the Log with the described task. This approach seems questionable, as without this "feature" there is no limit to description length anyways and now there is no way to change a description. In short while the recent update eliminates functionality it does not add any utility. 
  
There is now an ability to add sizes to the tasks. This is a free form text field added either after the describe command, ```descirbe <task>"<description>""<size>``` or as a standalone command. ```size <task> <size>```. This works as is but if commands are to accept optional parameters it may be prudent to require a command line option as otherwise the users intent may be mistaken.

Several major drawbacks currently exist in TM.  

- There is no way to edit entries in case of error.
- There is no way to delete entries.
- There is no way to manually add entries with times
- There is no way to list individual work periods

These drawbacks are severe enough that the system should be considered not usable as it stands. Most could be implemented without much difficulty. However the ability to delete entries other than the most recent may prove more difficult as there would be difficulty implementing a way for the user to specify which entry she wanted to remove. 



CompendiumCognexus
==================

open sourced version of Cognexus'own Compendium fork

This is almost 1:1 copy of Cognexus Compendium as it was released on Jan-16 2013 via e-mail:
(differences are listed at the end of this text)



KC Burgess Yakemovic kcby@cognexus.org via yahoogroups.com 
	
Jan 16
		
to compendium_dev
 

Compendium Developers --

Happy New Year, a bit late!

CogNexus Group is pleased to announce that we (finally!) have the source code and some documentation for CogNexus Compendium, v1.7.1 (Build 1355), available on our website.  The zip file containing the source files may be downloaded from this location:

http://www.cognexus.org/Compendium/OpenSource/CogNexus_Compendium_v1-7-1_B1355_open_source_release.zip

In addition to the source code, the following documentation is available:

1) CogNexus_Compendium_v1-7-1_source_rel_doc_v1.doc:   An overview document, describing at a high level what happened with the code, and outlining the changes made in each release from the point where we diverged to present.  *Note

http://www.cognexus.org/Compendium/OpenSource/CogNexus_Compendium_v1-7-1_source_rel_doc_v1.doc

2) begeman_mods_w_michelle_comments.doc:  The document that describes the changes that were given to Michelle Bachler (Compendium Institute), along with the notes Michelle made about which changes made it into Release 2.0.  Includes a key to what color means what that was in Michelle's email message. **Note

http://www.cognexus.org/Compendium/OpenSource/begemen_mods_w_michelle_comments.doc

3) Intro_to_C-PE--v1-7-0--non proprietary-v8.pdf:  a version of the user information for v1.7.0, that has all proprietary information removed. Since this feature is the 'major' improvement that isn't in release 2.0 (not counting bug fixes, and the Window 7 data/program split), I thought some sort of reasonable description of what it does and how it works might be useful.

http://www.cognexus.org/Compendium/OpenSource/Intro_to_C-PE--v1-7-0--non_proprietary-v8.pdf


I have tested the links above and believe that they work, but if you have problems with access to any of the files, please let me know.

Regards

-- kcby
    KC Burgess Yakemovic
    kc@cognexusgroup.com
    404-406-6424  (Atlanta - EDT)
    Director of Training
    Certified Dialogue Mapper  
    CogNexus Group:
    Building collective intelligence
    through shared understanding
    http://cognexusgroup.com

_______________

*Note: We are grateful for the opportunity to work with Southern California Edison, which has funded the vast majority of the changes we have made to Compendium.

**Note: A huge thanks to Michael Begeman - for having kept such good records, and being able to put his hands on them when we asked.  Another huge thanks to Michelle Bachler for reviewing Michael's records against the 2.0 code and documenting what she found! 

------
differences between released version and first version in this repository
a) .gitignore, README and LICENSE files added
b) removed java.exe which was originally part of the released pack
c) remove any .svn directory (project was managed via svn earlier)


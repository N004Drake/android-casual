/**
 * \brief creates a Google Plus formatted post from information provided to
 * Jenkins. If the job was started by a non-Source Code Modification, the output
 * will be blank. This way it is possible to detect weather or not it is a SCM
 * change or a manual build.
 *
 *
 * @see source:
 * https://code.google.com/p/android-casual/source/browse/trunk/X/Jenkins+Commit?spec=svn931&r=931#Jenkins%20Commit%2Fsrc%2Fcom%2Fcasual_dev%2FCommitDescription
 * @author AdamOutler adamoutler@casual-dev.com
 */
package com.casual_dev.CommitDescription;

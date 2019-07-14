/* Generated by AN DISI Unibo */ 
package it.unibo.kb

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Kb ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start kb")
					}
					 transition(edgeName="t01",targetState="handleUpdate",cond=whenDispatch("modelUpdate"))
				}	 
				state("handleUpdate") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(TARGET,VALUE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var Target=payloadArg(0)
												var Value=payloadArg(1)
												
								println("----------->KB[$Target][$Value]")
						}
					}
					 transition(edgeName="t02",targetState="handleUpdate",cond=whenDispatch("modelUpdate"))
				}	 
			}
		}
}

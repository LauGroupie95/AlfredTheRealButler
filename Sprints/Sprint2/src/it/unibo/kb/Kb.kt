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
						solve("consult('robotPosResolver.pl')","") //set resVar	
					}
					 transition(edgeName="t01",targetState="handle",cond=whenDispatch("modelUpdate"))
					transition(edgeName="t02",targetState="handle",cond=whenDispatch("modelRequest"))
				}	 
				state("handle") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelRequest(TARGET,PROP)"), Term.createTerm("modelRequest(TARGET,PROP)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var Target=payloadArg(0)
												var Prop=payloadArg(1)
												//var Sender=payloadArg(2)
								if(Target=="robot" && Prop=="location"){ solve("actualRobotPos(X,Y,O)","") //set resVar	
								if(currentSolution.isSuccess()) { 
														var X = getCurSol("X").toString()
														var Y = getCurSol("Y").toString()
														var O = getCurSol("O").toString()
								forward("modelResponse", "modelResponse($X,$Y,$O)" ,"maitre" ) 
								 }
								else
								{ forward("modelResponse", "modelResponse(error)" ,"maitre" ) 
								 }
								 }
						}
						if( checkMsgContent( Term.createTerm("modelUpdate(TARGET,VALUE)"), Term.createTerm("modelUpdate(TARGET,VALUE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var Target=payloadArg(0)
												var Value=payloadArg(1)
												
								if(Target=="robot"){ solve("updateRobotStateFromMove($Value)","") //set resVar	
								 }
						}
					}
					 transition(edgeName="t03",targetState="handle",cond=whenDispatch("modelUpdate"))
					transition(edgeName="t04",targetState="handle",cond=whenDispatch("modelRequest"))
				}	 
			}
		}
}

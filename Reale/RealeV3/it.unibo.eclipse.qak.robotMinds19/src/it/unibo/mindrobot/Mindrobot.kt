/* Generated by AN DISI Unibo */ 
package it.unibo.mindrobot

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Mindrobot ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			var obstacle = false
			val minDifferenceForBelance = 2
			var myagain = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Start mindrobot")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="handleEnvCond",cond=whenEvent("envCond"))
					transition(edgeName="t01",targetState="handleSonarRobot",cond=whenEvent("sonarRobot"))
					transition(edgeName="t02",targetState="handleModelChanged",cond=whenEvent("local_modelChanged"))
				}	 
				state("handleEnvCond") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("envCond(CONDTYPE)"), Term.createTerm("envCond(CMD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("robotCmd", "robotCmd(h)" ,"basicrobot" ) 
								forward("modelUpdate", "modelUpdate(robot,h)" ,"resourcemodel" ) 
						}
					}
					 transition( edgeName="goto",targetState="requestBelancer", cond=doswitch() )
				}	 
				state("requestBelancer") { //this:State
					action { //it:State
						println("requestBelancer")
						itunibo.robotRaspOnly.sonarBelancerOnlySupport.requestValue(  )
					}
					 transition(edgeName="t03",targetState="riadrizza",cond=whenEvent("sonarBelancer"))
				}	 
				state("riadrizza") { //this:State
					action { //it:State
						myagain = false
						if( checkMsgContent( Term.createTerm("sonar(DIFFERENCE)"), Term.createTerm("sonar(DIFFERENCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var diff = Integer.parseInt( payloadArg(0) )								
								println("riadrizza--> $diff")
								if(diff>2){ myagain = true
								forward("robotCmd", "robotCmd(a)" ,"basicrobot" ) 
								delay(50) 
								forward("robotCmd", "robotCmd(h)" ,"basicrobot" ) 
								 }
								if(diff<-minDifferenceForBelance){ myagain = true
								forward("robotCmd", "robotCmd(d)" ,"basicrobot" ) 
								delay(50) 
								forward("robotCmd", "robotCmd(h)" ,"basicrobot" ) 
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitchGuarded({myagain}) )
					transition( edgeName="goto",targetState="requestBelancer", cond=doswitchGuarded({! myagain}) )
				}	 
				state("handleSonarRobot") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								obstacle = Integer.parseInt( payloadArg(0) ) < 10 
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("handleModelChanged") { //this:State
					action { //it:State
						obstacle=false
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("modelChanged(TARGET,VALUE)"), Term.createTerm("modelChanged(robot,CMD)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("robotCmd", "robotCmd(${payloadArg(1)})" ,"basicrobot" ) 
						}
					}
					 transition( edgeName="goto",targetState="handleObstacle", cond=doswitchGuarded({obstacle}) )
					transition( edgeName="goto",targetState="waitCmd", cond=doswitchGuarded({! obstacle}) )
				}	 
				state("handleObstacle") { //this:State
					action { //it:State
						forward("robotCmd", "robotCmd(h)" ,"basicrobot" ) 
						forward("modelUpdate", "modelUpdate(robot,h)" ,"resourcemodel" ) 
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}

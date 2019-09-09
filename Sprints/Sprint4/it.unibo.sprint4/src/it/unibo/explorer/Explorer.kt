/* Generated by AN DISI Unibo */ 
package it.unibo.explorer

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Explorer ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
			var stepCounter = 0 
			var Move = ""
			var StepTime :Int  = 350	//for virtual
			var RotateTime = 300L	//for virtual
			var PauseTime  = 250L 
			var Direction = ""
			//-------------------------------
			var mapEmpty    = false
			val mapname     = "roomBoundary" 
			var Tback       = 0
			var NumStep     = 0
		
			var secondLap : Boolean = false
			var mustStop : Boolean = false
			
			var needExploreBound : Boolean =false
			var tableFound : Boolean =false
			var directionSud : Boolean =false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("Actor: Explorer; State: initial")
						solve("consult('moves.pl')","") //set resVar	
						itunibo.coap.observer.resourceObserverCoapClient.create( "coap://localhost:5683/resourcemodel"  )
						itunibo.planner.plannerUtil.initAI(  )
						itunibo.planner.moveUtils.showCurrentRobotState(  )
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
						println("Waiting for exploration cmd...")
					}
					 transition(edgeName="t011",targetState="handeCmd",cond=whenDispatch("doExplor"))
					transition(edgeName="t012",targetState="goToPosition",cond=whenEvent("goTo"))
				}	 
				state("handeCmd") { //this:State
					action { //it:State
						storeCurrentMessageForReply()
						needExploreBound=false
						if( checkMsgContent( Term.createTerm("doExplor(TARGET)"), Term.createTerm("doExplor(TARGET)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												needExploreBound = (payloadArg(0)=="bound")
												secondLap=false
						}
					}
					 transition( edgeName="goto",targetState="exploreBounds", cond=doswitchGuarded({needExploreBound}) )
					transition( edgeName="goto",targetState="exploreTale", cond=doswitchGuarded({! needExploreBound}) )
				}	 
				state("exploreBounds") { //this:State
					action { //it:State
						if(!secondLap){ println("Start explore bounds.")
						NumStep=0
						 }
					}
					 transition( edgeName="goto",targetState="rotateEast", cond=doswitchGuarded({secondLap}) )
					transition( edgeName="goto",targetState="detectPerimeter", cond=doswitchGuarded({! secondLap}) )
				}	 
				state("rotateEast") { //this:State
					action { //it:State
						NumStep=0
						Move="a"
						forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
					}
					 transition(edgeName="t013",targetState="detectPerimeter",cond=whenDispatch("rotationOk"))
				}	 
				state("detectPerimeter") { //this:State
					action { //it:State
						NumStep++
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitchGuarded({(NumStep<5)}) )
					transition( edgeName="goto",targetState="perimeterWalked", cond=doswitchGuarded({! (NumStep<5)}) )
				}	 
				state("goOneStepAhead") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.attemptTomoveAhead(myself ,StepTime, "onecellforward" )
					}
					 transition(edgeName="t014",targetState="handleStepOk",cond=whenDispatch("stepOk"))
					transition(edgeName="t015",targetState="checkingObject",cond=whenEvent("collision"))
				}	 
				state("handleStepOk") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.updateMapAfterAheadOk(myself)
						delay(500) 
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitch() )
				}	 
				state("checkingObject") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("collision(OBJECT)"), Term.createTerm("collision(OBJECT)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								val ObjName = payloadArg(0)
								println("OGGETTO IN COLLISIONE: $ObjName")
								if((ObjName.equals("pantry") || ObjName.equals("dishwasher") || ObjName.equals("fridge")|| ObjName.equals("table"))){ 
												val XTemp = itunibo.planner.plannerUtil.getPosX()
												val YTemp = itunibo.planner.plannerUtil.getPosY()	
												tableFound	=ObjName.equals("table")		
								forward("modelUpdateMap", "modelUpdateMap($ObjName,$XTemp,$YTemp)" ,"kb" ) 
								 }
						}
					}
					 transition( edgeName="goto",targetState="handleStepFail", cond=doswitchGuarded({needExploreBound}) )
					transition( edgeName="goto",targetState="handleStepFailTable", cond=doswitchGuarded({! needExploreBound}) )
				}	 
				state("handleStepFail") { //this:State
					action { //it:State
						delay(500) 
						val MapStr =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$MapStr)" ,"resourcemodel" ) 
						itunibo.planner.plannerUtil.wallFound(  )
						if(secondLap){ Move="d"
						forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						 }
						else
						 { Move="a"
						 forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						 itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						  }
					}
					 transition(edgeName="t016",targetState="detectPerimeter",cond=whenDispatch("rotationOk"))
				}	 
				state("perimeterWalked") { //this:State
					action { //it:State
						if(!secondLap){ println("FINAL MAP")
						itunibo.planner.moveUtils.showCurrentRobotState(  )
						itunibo.planner.plannerUtil.saveMap( mapname  )
						secondLap = true
						 }
						else
						 { mustStop = true
						  }
					}
					 transition( edgeName="goto",targetState="endOfJobBounds", cond=doswitchGuarded({mustStop}) )
					transition( edgeName="goto",targetState="exploreBounds", cond=doswitchGuarded({! mustStop}) )
				}	 
				state("endOfJobBounds") { //this:State
					action { //it:State
						delay(500) 
						forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						Move="d"
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						println("Perimeter completely walked. Exit.")
					}
					 transition(edgeName="t017",targetState="reply",cond=whenDispatch("rotationOk"))
				}	 
				state("reply") { //this:State
					action { //it:State
						replyToCaller("endExplor", "endExplor(ok)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("exploreTale") { //this:State
					action { //it:State
						println("Start explore table.")
						tableFound=false
					}
					 transition( edgeName="goto",targetState="goOneStepAhead", cond=doswitch() )
				}	 
				state("handleStepFailTable") { //this:State
					action { //it:State
						
								val MapStr =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$MapStr)" ,"resourcemodel" ) 
						itunibo.planner.plannerUtil.wallFound(  )
					}
					 transition( edgeName="goto",targetState="endOfJobTable", cond=doswitchGuarded({tableFound}) )
					transition( edgeName="goto",targetState="askOrientation", cond=doswitchGuarded({! tableFound}) )
				}	 
				state("askOrientation") { //this:State
					action { //it:State
						forward("modelRequest", "modelRequest(robot,location)" ,"kb" ) 
					}
					 transition(edgeName="t018",targetState="rotateBefore",cond=whenDispatch("modelRobotResponse"))
				}	 
				state("rotateBefore") { //this:State
					action { //it:State
						if( checkMsgContent( Term.createTerm("modelRobotResponse(X,Y,O)"), Term.createTerm("modelRobotResponse(X,Y,O)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								val actualOrientation = payloadArg(2)
								if(actualOrientation=="sud"){ Move="a"
								forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
								itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
								 }
								if(actualOrientation=="nord"){ Move="d"
								forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
								itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
								 }
						}
					}
					 transition(edgeName="t019",targetState="moveOne",cond=whenDispatch("rotationOk"))
				}	 
				state("moveOne") { //this:State
					action { //it:State
						delay(500) 
						itunibo.planner.moveUtils.attemptTomoveAhead(myself ,StepTime, "onecellforward" )
					}
					 transition(edgeName="t020",targetState="rotateAfter",cond=whenDispatch("stepOk"))
					transition(edgeName="t021",targetState="checkingObject",cond=whenEvent("collision"))
				}	 
				state("rotateAfter") { //this:State
					action { //it:State
						if(Move=="a"){ forward("onerotationstep", "onerotationstep(a)" ,"onerotateforward" ) 
						 }
						else
						 { forward("onerotationstep", "onerotationstep(d)" ,"onerotateforward" ) 
						  }
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
					}
					 transition(edgeName="t022",targetState="goOneStepAhead",cond=whenDispatch("rotationOk"))
				}	 
				state("endOfJobTable") { //this:State
					action { //it:State
						println("Map after explore table")
						itunibo.planner.plannerUtil.showMap(  )
						itunibo.planner.plannerUtil.saveMap( mapname  )
						println("Table exploration end.")
						replyToCaller("endExplor", "endExplor(ok)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("goToPosition") { //this:State
					action { //it:State
						storeCurrentMessageForReply()
						if( checkMsgContent( Term.createTerm("goTo(X,Y)"), Term.createTerm("goTo(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								
												var X = payloadArg(0).toInt()
												var Y = payloadArg(1).toInt()
												//var O = payloadArg(2)
								solve("direction(D)","") //set resVar	
								println("Actor: Explorer; State: goToPosition; Payload: direction at start: ${getCurSol("D").toString()}")
								itunibo.planner.plannerUtil.showMap(  )
								itunibo.planner.plannerUtil.setGoal( X, Y  )
								itunibo.planner.moveUtils.doPlan(myself)
						}
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitchGuarded({itunibo.planner.moveUtils.existPlan()}) )
					transition( edgeName="goto",targetState="endOfJobFail", cond=doswitchGuarded({! itunibo.planner.moveUtils.existPlan()}) )
				}	 
				state("executePlannedActions") { //this:State
					action { //it:State
						solve("retract(move(M))","") //set resVar	
						if(currentSolution.isSuccess()) { Move = getCurSol("M").toString()
						 }
						else
						{ Move = ""
						 }
					}
					 transition( edgeName="goto",targetState="doTheMove", cond=doswitchGuarded({(Move.length>0) }) )
					transition( edgeName="goto",targetState="endOfJobOk", cond=doswitchGuarded({! (Move.length>0) }) )
				}	 
				state("doTheMove") { //this:State
					action { //it:State
						if(Move=="a" || Move=="d" ){ forward("onerotationstep", "onerotationstep($Move)" ,"onerotateforward" ) 
						 }
						else
						 { forward("onestep", "onestep($StepTime)" ,"onecellforward" ) 
						  }
					}
					 transition(edgeName="t023",targetState="handleStepOk",cond=whenDispatch("stepOk"))
					transition(edgeName="t024",targetState="endOfJobFail",cond=whenDispatch("stepFail"))
					transition(edgeName="t025",targetState="handleStepOk",cond=whenDispatch("rotationOk"))
				}	 
				state("handleStepOk") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						delay(PauseTime)
					}
					 transition( edgeName="goto",targetState="executePlannedActions", cond=doswitch() )
				}	 
				state("rotateSouth") { //this:State
					action { //it:State
						Move="a"
						delay(PauseTime)
						forward("onerotationstep", "onerotationstep($Move)" ,"onerotateforward" ) 
					}
					 transition(edgeName="t026",targetState="checkSouth",cond=whenDispatch("rotationOk"))
				}	 
				state("checkSouth") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.doPlannedMove(myself ,Move )
						solve("direction(D)","") //set resVar	
						Direction = getCurSol("D").toString() 
						println("Actor: Explorer; State: rotateSouth; Payload: $Direction")
					}
					 transition( edgeName="goto",targetState="rotateSouth", cond=doswitchGuarded({(Direction!="downDir")}) )
					transition( edgeName="goto",targetState="endOfJobOk", cond=doswitchGuarded({! (Direction!="downDir")}) )
				}	 
				state("endOfJobOk") { //this:State
					action { //it:State
						println("Explorer: on the target cell!")
						replyToCaller("goToOk", "goToOk(ok)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("endOfJobFail") { //this:State
					action { //it:State
						println("Actor: Explorer; State: handleStepFail; Payload: Fail step :(")
						itunibo.planner.plannerUtil.showMap(  )
						println("Actor: Explorer; State: handleStepFail; Payload: Replan and return at home.")
						replyToCaller("goToFail", "goToFail(fail)")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}

<?php

namespace App\Http\Controllers;

use App\Models\Challenge; // Ensure the correct namespace for your Challenge model
use Illuminate\Http\Request;

class ChallengeController extends Controller
{
    /**
     * Display a listing of the challenges.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $challenges = Challenge::all();
        $activePage = 'challenges';
        $activeButton = 'challenges';
        $navName = 'Challenges';
        return view('pages.challenges', compact('challenges', 'activePage', 'activeButton', 'navName'));
    }

    /**
     * Show the form for creating a new challenge.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        $activePage = 'create-challenge';
        $activeButton = 'challenges';
        $navName = 'Create Challenge';
        return view('challenges.create', compact('activePage', 'activeButton', 'navName'));
    }

    /**
     * Store a newly created challenge in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $request->validate([

            'challengeNo' => 'required|unique:challenges',

            
            'challengeName' => 'required',
    
            'attemptDuration' => 'required',
    
            'noOfQuestions' => 'required|integer|min:1',
    
            'overallMark' => 'required|integer|min:1',
    
            'openDate' => 'required|date',
    
            'closeDate' => 'required|date|after_or_equal:openDate',
    
        ]);
    
    
        $challenge = new Challenge([
    
            'challengeNo' => $request->get('challengeNo'),

            'challengeName' => $request->get('challengeName'),
    
            'attemptDuration' => $request->get('attemptDuration'),
    
            'noOfQuestions' => $request->get('noOfQuestions'),
    
            'overallMark' => $request->get('overallMark'),
    
            'openDate' => $request->get('openDate'),
    
            'closeDate' => $request->get('closeDate'),
    
        ]);

        $challenge->save();

        return redirect()->route('challenges.index')->with('success', 'Challenge has been added');
    }
}

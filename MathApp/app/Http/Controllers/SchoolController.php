<?php

namespace App\Http\Controllers;

use App\Models\School;
use Illuminate\Http\Request;

class SchoolController extends Controller
{
    /**
     * Display a listing of the schools.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $schools = School::all();
        $activePage = 'schools';
        $activeButton = 'schools';
        $navName = 'Schools';
        return view('pages.schools', compact('schools', 'activePage', 'activeButton', 'navName'));
    }

    /**
     * Show the form for creating a new school.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        $activePage = 'create-school';
        $activeButton = 'schools';
        $navName = 'Create School';
        return view('schools.create', compact('activePage', 'activeButton', 'navName'));
    }

    /**
     * Store a newly created school in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        // Validate the form data
        $validatedData = $request->validate([
            'schoolRegNo' => 'required|unique:schools',
            'schoolName' => 'required',
            'district' => 'required',
            'schoolRepID' => 'required',
            'emailAddress' => 'required|email|unique:schools',
            'password' => 'required',
        ]);
    
        // Create a new school instance
        $school = new School();
        $school->schoolRegNo = $validatedData['schoolRegNo'];
        $school->schoolName = $validatedData['schoolName'];
        $school->district = $validatedData['district'];
        $school->schoolRepID = $validatedData['schoolRepID'];
        $school->emailAddress = $validatedData['emailAddress'];
        $school->password = bcrypt($validatedData['password']);
    
        // Save the school instance
        $school->save();
    
        // Redirect to the schools index page
        return redirect()->route('schools.index')->with('success', 'School created successfully!');
    }
}

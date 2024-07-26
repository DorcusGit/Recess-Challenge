@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Schools</h1>
    <a href="{{ route('schools.create') }}" class="btn btn-primary">Add New School</a>

    @if(session('success'))
        <div class="alert alert-success">{{ session('success') }}</div>
    @endif

    <table class="table">
        <thead>
            <tr>
                <th>School Reg No</th>
                <th>School Name</th>
                <th>District</th>
                <th>Email Address</th>
            </tr>
        </thead>
        <tbody>
            @foreach($schools as $school)
                <tr>
                    <td>{{ $school->schoolRegNo }}</td>
                    <td>{{ $school->schoolName }}</td>
                    <td>{{ $school->district }}</td>
                    <td>{{ $school->emailAddress }}</td>
                </tr>
            @endforeach
        </tbody>
    </table>
</div>
@endsection